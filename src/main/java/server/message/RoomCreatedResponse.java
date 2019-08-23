package server.message;

import io.netty.buffer.ByteBuf;

public class RoomCreatedResponse extends Message {
    public int roomId;

    public RoomCreatedResponse(int roomId) {
        this.roomId = roomId;
    }

    RoomCreatedResponse() {
    }

    @Override
    public Header getHeader() {
        return Headers.ROOM_CREATED_RESPONSE;
    }

    @Override
    public void readFromBuffer(ByteBuf buffer) {
        roomId = buffer.readInt();
    }

    @Override
    protected void writeToBuffer(ByteBuf buffer) {
        buffer.writeInt(roomId);
    }

    @Override
    public String toString() {
        return "RoomCreatedResponse{" +
                "roomId=" + roomId +
                '}';
    }
}
