package server.message;

import server.BufferTools;
import io.netty.buffer.ByteBuf;

public class RegistrationRequest extends Message {
    public String secret;
    public long id;

    public RegistrationRequest(String secret, long id) {
        this.secret = secret;
        this.id = id;
    }

    RegistrationRequest() {
    }

    @Override
    public Header getHeader() {
        return Headers.REGISTRATION_REQUEST;
    }

    @Override
    public void readFromBuffer(ByteBuf buffer) {
        secret = BufferTools.readString(buffer);
        id = buffer.readLong();
    }

    @Override
    public void writeToBuffer(ByteBuf buffer) {
        BufferTools.writeString(secret, buffer);
        buffer.writeLong(id);
    }

    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "secret='" + secret + '\'' +
                ", id=" + id +
                '}';
    }
}
