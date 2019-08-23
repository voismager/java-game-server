package server.message;

import io.netty.buffer.ByteBuf;

public class JoinRoomRequest extends Message {
    public int roomId;

    public JoinRoomRequest(int roomId) {
        this.roomId = roomId;
    }

    JoinRoomRequest() {
    }

    @Override
    public Header getHeader() {
        return Headers.JOIN_ROOM_REQUEST;
    }

    @Override
    public void readFromBuffer(ByteBuf buffer) {
        roomId = buffer.readInt();
    }

    @Override
    public void writeToBuffer(ByteBuf buffer) {
        buffer.writeInt(roomId);
    }

    @Override
    public String toString() {
        return "JoinRoomRequest{" +
                "roomId=" + roomId +
                '}';
    }
}
