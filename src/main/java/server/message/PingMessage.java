package server.message;

import io.netty.buffer.ByteBuf;

public class PingMessage extends Message {
    @Override
    public Header getHeader() {
        return Headers.PING;
    }

    @Override
    public void readFromBuffer(ByteBuf buffer) {
    }

    @Override
    public void writeToBuffer(ByteBuf buffer) {
    }
}
