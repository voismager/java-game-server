package server.message;

import io.netty.buffer.ByteBuf;

public class DisconnectRequest extends Message {
    public byte cause;

    public DisconnectRequest(byte cause) {
        this.cause = cause;
    }

    DisconnectRequest() {
    }

    @Override
    public Header getHeader() {
        return Headers.DISCONNECT_REQUEST;
    }

    @Override
    public void readFromBuffer(ByteBuf buffer) {
        cause = buffer.readByte();
    }

    @Override
    public void writeToBuffer(ByteBuf buffer) {
        buffer.writeByte(cause);
    }

    @Override
    public String toString() {
        return "DisconnectRequest{" +
                "cause=" + cause +
                '}';
    }
}
