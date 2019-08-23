package server.globalsession;

import server.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ChannelHandler.Sharable
public class ResponseEncoder extends ChannelOutboundHandlerAdapter {
    private final ByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;

    private final Logger logger = LogManager.getLogger(ResponseEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        Message response = (Message) msg;
        ByteBuf buffer = allocator.buffer();
        response.encode(buffer);
        ctx.write(buffer, promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warn("Exception caught", cause);
    }
}
