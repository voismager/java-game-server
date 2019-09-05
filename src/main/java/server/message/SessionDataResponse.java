package server.message;

import io.netty.buffer.ByteBuf;
import server.payload.SessionData;

public class SessionDataResponse extends Message {
    public SessionData data;

    SessionDataResponse() {
    }

    public SessionDataResponse(SessionData data) {
        this.data = data;
    }

    @Override
    public Header getHeader() {
        return Headers.SESSION_DATA_RESPONSE;
    }

    @Override
    public void readFromBuffer(ByteBuf buffer) {
        data = new SessionData(buffer);
    }

    @Override
    protected void writeToBuffer(ByteBuf buffer) {
        data.writeToBuffer(buffer);
    }

    @Override
    public String toString() {
        return "SessionDataResponse{" +
                "data=" + data +
                '}';
    }
}
