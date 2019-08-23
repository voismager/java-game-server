package server.message;

import io.netty.buffer.ByteBuf;

public class PlayerLeftResponse extends Message {
    public long player;
    public int room;

    public PlayerLeftResponse(long player, int room) {
        this.player = player;
        this.room = room;
    }

    PlayerLeftResponse() {
    }

    @Override
    public Header getHeader() {
        return Headers.PLAYER_LEFT_RESPONSE;
    }

    @Override
    public void readFromBuffer(ByteBuf buffer) {
        player = buffer.readLong();
        room = buffer.readInt();
    }

    @Override
    protected void writeToBuffer(ByteBuf buffer) {
        buffer.writeLong(player);
        buffer.writeInt(room);
    }

    @Override
    public String toString() {
        return "PlayerLeftResponse{" +
                "player=" + player +
                ", room=" + room +
                '}';
    }
}
