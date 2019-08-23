package server.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public enum Headers implements Header {
    REGISTRATION_REQUEST(RegistrationRequest::new),
    CREATE_ROOM_REQUEST(new Supplier<Message>() {
        final CreateRoomRequest INSTANCE = new CreateRoomRequest();
        public Message get() { return INSTANCE; }
    }),
    JOIN_ROOM_REQUEST(JoinRoomRequest::new),
    LEAVE_ROOM_REQUEST(LeaveRoomRequest::new),
    SET_ROOM_PROPERTY_REQUEST(SetRoomPropertyRequest::new),
    SET_PLAYER_PROPERTY_REQUEST(SetPlayerPropertyRequest::new),
    DISCONNECT_REQUEST(DisconnectRequest::new),
    PING(new Supplier<Message>() {
        final PingMessage INSTANCE = new PingMessage();
        public Message get() { return INSTANCE; }
    }),
    REGISTRATION_SUCCEED_RESPONSE(RegistrationSucceedResponse::new),
    PLAYER_CONNECTED_RESPONSE(PlayerConnectedResponse::new),
    PLAYER_DISCONNECTED_RESPONSE(PlayerDisconnectedResponse::new),
    ROOM_CREATED_RESPONSE(RoomCreatedResponse::new),
    ROOM_DESTROYED_RESPONSE(RoomDestroyedResponse::new),
    PLAYER_JOINED_RESPONSE(PlayerJoinedResponse::new),
    PLAYER_LEFT_RESPONSE(PlayerLeftResponse::new),
    ROOM_PROPERTY_SET_RESPONSE(RoomPropertySetResponse::new),
    PLAYER_PROPERTY_SET_RESPONSE(PlayerPropertySetResponse::new),
    PLAYER_UNSUBSCRIBED_RESPONSE(PlayerUnsubscribedResponse::new);

    private static final List<Header> HEADERS;

    static {
        HEADERS = new ArrayList<>(Arrays.asList(values()));
    }

    public static Message decode(ByteBuf buffer) {
        byte id = buffer.readByte();
        Header h = HEADERS.get(id);

        Message message = h.getEmptyMessage();
        message.readFromBuffer(buffer);

        return message;
    }

    public static ChannelMessage decode(Channel channel, ByteBuf buffer) {
        return new ChannelMessage(channel, decode(buffer));
    }

    public static synchronized Header registerMessage(Supplier<CustomMessage> emptyMessageGetter) {
        final byte id = (byte) HEADERS.size();

        Header h = new Header() {
            @Override
            public byte id() { return id; }
            @Override
            public CustomMessage getEmptyMessage() {
                CustomMessage m = emptyMessageGetter.get();
                m.setHeader(this);
                return m;
            }
            @Override
            public final int hashCode() { return id; }
            @Override
            public final boolean equals(Object o) { return this == o; }
        };

        HEADERS.add(h);
        return h;
    }

    private final Supplier<Message> getter;

    Headers(Supplier<Message> getter) {
        this.getter = getter;
    }

    @Override
    public byte id() {
        return (byte) ordinal();
    }

    @Override
    public Message getEmptyMessage() {
        return getter.get();
    }
}
