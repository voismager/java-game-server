package server.payload;

import io.netty.buffer.ByteBuf;

public abstract class Payload {
    public Payload() { }

    public Payload(ByteBuf buffer) {
        readFromBuffer(buffer);
    }

    public abstract void writeToBuffer(ByteBuf buffer);

    public abstract void readFromBuffer(ByteBuf buffer);
}
