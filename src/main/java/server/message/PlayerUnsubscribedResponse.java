package server.message;

import io.netty.buffer.ByteBuf;

public class PlayerUnsubscribedResponse extends Message {
    public long player;
    public int gameRoom;

    public PlayerUnsubscribedResponse(long player, int gameRoom) {
        this.player = player;
        this.gameRoom = gameRoom;
    }

    PlayerUnsubscribedResponse() {
    }

    @Override
    public Header getHeader() {
        return Headers.PLAYER_UNSUBSCRIBED_RESPONSE;
    }

    @Override
    public void readFromBuffer(ByteBuf buffer) {
        player = buffer.readLong();
        gameRoom = buffer.readInt();
    }

    @Override
    protected void writeToBuffer(ByteBuf buffer) {
        buffer.writeLong(player);
        buffer.writeInt(gameRoom);
    }

    @Override
    public String toString() {
        return "PlayerUnsubscribedResponse{" +
                "player=" + player +
                ", gameRoom=" + gameRoom +
                '}';
    }
}
