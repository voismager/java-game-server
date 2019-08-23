package server.message;

import server.payload.FullSessionData;
import io.netty.buffer.ByteBuf;

public class RegistrationSucceedResponse extends Message {
    public FullSessionData data;

    RegistrationSucceedResponse() {
    }

    public RegistrationSucceedResponse(FullSessionData data) {
        this.data = data;
    }

    @Override
    public Header getHeader() {
        return Headers.REGISTRATION_SUCCEED_RESPONSE;
    }

    @Override
    public void readFromBuffer(ByteBuf buffer) {
        data = new FullSessionData(buffer);
    }

    @Override
    protected void writeToBuffer(ByteBuf buffer) {
        data.writeToBuffer(buffer);
    }

    @Override
    public String toString() {
        return "RegistrationSucceedResponse{" +
                "data=" + data +
                '}';
    }
}
