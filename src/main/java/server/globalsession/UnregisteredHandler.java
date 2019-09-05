package server.globalsession;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.Session;
import server.message.ChannelMessage;
import server.message.Header;
import server.message.Headers;

import java.util.Set;

@ChannelHandler.Sharable
public class UnregisteredHandler extends ChannelInboundHandlerAdapter {
    private final Session session;
    private final Set<Header> headers;

    private static final Logger logger = LogManager.getLogger(UnregisteredHandler.class);

    public UnregisteredHandler(Session session) {
        this.session = session;
        this.headers = Headers.setOf(session.unregisteredHeaders());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;

        ChannelMessage request = Headers.decode(ctx.channel(), buf);

        if (this.headers.contains(request.request.getHeader())) {
            session.addRequest(request);
        }

        ReferenceCountUtil.release(buf);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;

            if (e.state() == IdleState.WRITER_IDLE) {
                logger.info("Timeout!");
                ctx.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warn("Exception caught:", cause);
    }
}
