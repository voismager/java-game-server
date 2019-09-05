package server.globalsession;

import server.*;
import server.payload.FullSessionData;
import server.payload.PlayerData;
import server.property.Properties;
import server.property.PropertyKey;
import server.message.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.globalsession.requesthandlers.Handler;
import server.messagesender.MessageSender;
import server.messagesender.SessionSender;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import static server.ShortMessageCodes.SERVER;
import static server.ShortMessageCodes.TIMEOUT;

public class GlobalSession implements Session {
    private final static Logger logger = LogManager.getLogger(GlobalSession.class);

    private final IdGenerator idGenerator;
    private final PlayerAPI playerAPI;
    private final IntFunction<GameSession> gameSessionBuilder;
    private final Map<Header, Handler> handlers;
    private final BlockingQueue<ChannelMessage> messages;
    private final Map<Channel, RPlayer> registered;
    private final Int2ObjectMap<SessionRoom> rooms;
    private final SessionSender sessionSender;
    private final TCPServer server;

    private final Map<PropertyKey, Object> properties;

    public GlobalSession(PlayerAPI playerAPI, IntFunction<GameSession> gameSessionBuilder, Handler... handlers) {
        this.idGenerator = new IdGenerator();
        this.playerAPI = playerAPI;
        this.gameSessionBuilder = gameSessionBuilder;
        this.handlers = new HashMap<>();
        this.messages = new LinkedBlockingQueue<>();
        this.sessionSender = new SessionSender(this);
        this.registered = new HashMap<>();
        this.rooms = new Int2ObjectOpenHashMap<>();

        final ResponseEncoder encoder = new ResponseEncoder();
        final UnregisteredHandler hlr = new UnregisteredHandler(this);

        this.server = new TCPServer(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel c) {
                c.pipeline()
                        .addLast(new IdleStateHandler(17, 7, 0))
                        .addLast(new LengthFieldBasedFrameDecoder(Short.MAX_VALUE, 0, 2, 0, 2))
                        .addLast(new LengthFieldPrepender(2))
                        .addLast(encoder)
                        .addLast(hlr);
            }
        });

        this.properties = new HashMap<>();
        for (PropertyKey key : Properties.getAllByType(PropertyKey.SESSION_PROPERTY)) {
            this.properties.put(key, key.defaultValue());
        }

        for (Handler handler : handlers) {
            for (Header header : handler.headers()) {
                bind(header, handler);
            }
        }
    }

    public void bind(Header header, Handler handler) {
        this.handlers.put(header, handler);
    }

    @Override
    public String type() {
        return "Global Session";
    }

    @Override
    public Collection<Header> unregisteredHeaders() {
        return Arrays.asList(Headers.REGISTRATION_REQUEST, Headers.SESSION_DATA_REQUEST);
    }

    @Override
    public void start(InetSocketAddress address) throws InterruptedException {
        this.server.start(address, 1);
        this.messages.clear();

        new Thread(() -> {
            while (true) {
                try {
                    final ChannelMessage msg = this.messages.take();
                    logger.info(msg);

                    if (msg.request != null) {
                        handlers.get(msg.request.getHeader()).accept(this, msg);
                    } else break;

                } catch (RuntimeException e) {
                    logger.warn("Local session exception", e);
                } catch (InterruptedException e) {
                    logger.error("Local session was interrupted");
                    Thread.currentThread().interrupt();
                    cleanup();
                }
            }

            cleanup();

        }, "Host Session").start();
    }

    @Override
    public void stop() {
        this.addRequest(new ChannelMessage(null, null));
    }

    @Override
    public void addRequest(ChannelMessage msg) {
        this.messages.offer(msg);
    }

    @Override
    public void setProperty(PropertyKey property, Object value) {
        properties.put(property, value);
    }

    @Override
    public Object getProperty(PropertyKey property) {
        return properties.get(property);
    }

    @Override
    public Collection<RPlayer> getRegistered() {
        return registered.values();
    }

    @Override
    public Stream<SessionRoom> getRoomsStream() {
        return rooms.values().stream();
    }

    public void close(Channel channel) {
        this.server.closeChild(channel);
    }

    public SessionRoom createRoom() {
        int id = this.idGenerator.generate();

        SessionRoom room = new SessionRoom(id, gameSessionBuilder.apply(id));

        this.rooms.put(id, room);

        this.sessionSender
                .addAll()
                .sendMessage(new RoomCreatedResponse(id))
                .flush();

        return room;
    }

    public void destroyRoom(int room) {
        this.rooms.remove(room);
        this.idGenerator.free(room);

        this.sessionSender
                .addAll()
                .sendMessage(new RoomDestroyedResponse(room))
                .flush();
    }

    public RPlayer getRegistered(Channel channel) {
        return registered.get(channel);
    }

    public void unregister(RPlayer player, byte cause) {
        this.registered.remove(player.channel);
        this.server.closeChild(player.channel);
        this.sessionSender
                .addAll()
                .sendMessage(new PlayerDisconnectedResponse(player.player.id, cause))
                .flush();
    }

    public SessionRoom getRoomByPlayer(RPlayer player) {
        return rooms.values().stream()
                .filter(r -> r.hasMember(player))
                .findAny().orElse(null);
    }

    public SessionRoom getRoomById(int id) {
        return this.rooms.get(id);
    }

    public MessageSender getSessionSender() {
        return sessionSender;
    }

    public void register(RPlayer player) {
        this.server.removeLastFromChild(player.channel);
        this.server.addLastToChild(player.channel, new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                final ByteBuf buf = (ByteBuf) msg;

                ChannelMessage request = Headers.decode(ctx.channel(), buf);

                if (request.request.getHeader() != Headers.PING && request.request.getHeader() != Headers.REGISTRATION_REQUEST) {
                    addRequest(request);
                }

                ReferenceCountUtil.release(buf);
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) {
                if (isRegistered(ctx.channel())) {
                    addRequest(new ChannelMessage(
                            ctx.channel(),
                            new DisconnectRequest(TIMEOUT))
                    );
                }
            }

            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                if (evt instanceof IdleStateEvent) {
                    IdleStateEvent e = (IdleStateEvent) evt;

                    if (e.state() == IdleState.WRITER_IDLE) {
                        ctx.writeAndFlush(Headers.PING.getEmptyMessage());
                    }

                    else if (e.state() == IdleState.READER_IDLE) {
                        logger.error("Timeout!");
                        ctx.close();
                    }
                }
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                logger.error("Exception caught:", cause);
            }
        });

        registered.put(player.channel, player);

        sessionSender
                .addOne(player)
                .sendMessage(new RegistrationSucceedResponse(new FullSessionData(this)))
                .flush()
                .addAllExcept(player)
                .sendMessage(new PlayerConnectedResponse(new PlayerData(player)))
                .flush();
    }

    private boolean isRegistered(Channel channel) {
        return registered.containsKey(channel);
    }

    public Player getPlayerInformation(long id) {
        try {
            return playerAPI.getPlayerInformation(id);

        } catch (Exception e) {
            logger.error("Can't get external player information. Using random name. {}", e.getMessage());
            return new Player(id, "Player " + System.currentTimeMillis());
        }
    }

    private void cleanup() {
        try {
            this.sessionSender.addAll();
            this.registered.values().forEach(pl -> sessionSender.sendMessage(new PlayerDisconnectedResponse(pl.player.id, SERVER)));
            this.registered.clear();
            this.sessionSender.flush();
            this.rooms.values().forEach(SessionRoom::cleanup);
            this.server.stop();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
