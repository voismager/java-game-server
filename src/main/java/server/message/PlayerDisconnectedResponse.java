package server.message;

import io.netty.buffer.ByteBuf;

public class PlayerDisconnectedResponse extends Message {
    public long player;
    public byte cause;

    public PlayerDisconnectedResponse(long player, byte cause) {
        this.player = player;
        this.cause = cause;
    }

    PlayerDisconnectedResponse() {
    }

    @Override
    public Header getHeader() {
        return Headers.PLAYER_DISCONNECTED_RESPONSE;
    }

    @Override
    public void readFromBuffer(ByteBuf buffer) {
        player = buffer.readLong();
        cause = buffer.readByte();
    }

    @Override
    protected void writeToBuffer(ByteBuf buffer) {
        buffer.writeLong(player);
        buffer.writeByte(cause);
    }

    @Override
    public String toString() {
        return "PlayerDisconnectedResponse{" +
                "player=" + player +
                ", cause=" + cause +
                '}';
    }
}
