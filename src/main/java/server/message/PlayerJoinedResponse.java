package server.message;

import io.netty.buffer.ByteBuf;

public class PlayerJoinedResponse extends Message {
    public long player;
    public int room;

    public PlayerJoinedResponse(long player, int room) {
        this.player = player;
        this.room = room;
    }

    PlayerJoinedResponse() {
    }

    @Override
    public Header getHeader() {
        return Headers.PLAYER_JOINED_RESPONSE;
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
        return "PlayerJoinedResponse{" +
                "player=" + player +
                ", room=" + room +
                '}';
    }
}
