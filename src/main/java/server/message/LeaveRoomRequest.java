package server.message;

import io.netty.buffer.ByteBuf;

public class LeaveRoomRequest extends Message {
    public int roomId;

    public LeaveRoomRequest(int roomId) {
        this.roomId = roomId;
    }

    LeaveRoomRequest() {
    }

    @Override
    public Header getHeader() {
        return Headers.LEAVE_ROOM_REQUEST;
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
        return "LeaveRoomRequest{" +
                "roomId=" + roomId +
                '}';
    }
}
