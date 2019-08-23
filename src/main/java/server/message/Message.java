package server.message;

import io.netty.buffer.ByteBuf;

public abstract class Message {
    public abstract Header getHeader();

    public final void encode(ByteBuf buffer) {
        buffer.writeByte(getHeader().id());
        writeToBuffer(buffer);
    }

    public abstract void readFromBuffer(ByteBuf buffer);

    protected abstract void writeToBuffer(ByteBuf buffer);
}
