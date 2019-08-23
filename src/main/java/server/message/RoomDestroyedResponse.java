package server.message;

import io.netty.buffer.ByteBuf;

public class RoomDestroyedResponse extends Message {
    public int roomId;

    public RoomDestroyedResponse(int roomId) {
        this.roomId = roomId;
    }

    RoomDestroyedResponse() {
    }

    @Override
    public Header getHeader() {
        return Headers.ROOM_DESTROYED_RESPONSE;
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
        return "RoomDestroyedResponse{" +
                "roomId=" + roomId +
                '}';
    }
}
