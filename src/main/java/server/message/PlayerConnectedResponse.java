package server.message;

import server.payload.PlayerData;
import io.netty.buffer.ByteBuf;

public class PlayerConnectedResponse extends Message {
    public PlayerData data;

    public PlayerConnectedResponse(PlayerData data) {
        this.data = data;
    }

    PlayerConnectedResponse() {
    }

    @Override
    public Header getHeader() {
        return Headers.PLAYER_CONNECTED_RESPONSE;
    }

    @Override
    public void readFromBuffer(ByteBuf buffer) {
        data = new PlayerData(buffer);
    }

    @Override
    protected void writeToBuffer(ByteBuf buffer) {
        data.writeToBuffer(buffer);
    }

    @Override
    public String toString() {
        return "PlayerConnectedResponse{" +
                "data=" + data +
                '}';
    }
}
