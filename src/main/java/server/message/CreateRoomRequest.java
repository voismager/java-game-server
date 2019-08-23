package server.message;

import io.netty.buffer.ByteBuf;

public class CreateRoomRequest extends Message {
    @Override
    public Header getHeader() {
        return Headers.CREATE_ROOM_REQUEST;
    }

    @Override
    public void readFromBuffer(ByteBuf buffer) {
    }

    @Override
    public void writeToBuffer(ByteBuf buffer) {
    }
}
