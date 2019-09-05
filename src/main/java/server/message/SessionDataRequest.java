package server.message;

import io.netty.buffer.ByteBuf;

public class SessionDataRequest extends Message {
    @Override
    public Header getHeader() {
        return Headers.SESSION_DATA_REQUEST;
    }

    @Override
    public void readFromBuffer(ByteBuf buffer) {
    }

    @Override
    protected void writeToBuffer(ByteBuf buffer) {
    }
}
