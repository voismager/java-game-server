package server.message;

import server.property.Properties;
import server.property.PropertyKey;
import io.netty.buffer.ByteBuf;

public class PlayerPropertySetResponse extends Message {
    public long player;
    public PropertyKey property;
    public Object value;

    public PlayerPropertySetResponse(long player, PropertyKey property, Object value) {
        this.player = player;
        this.property = property;
        this.value = value;
    }

    PlayerPropertySetResponse() {
    }

    @Override
    public Header getHeader() {
        return Headers.PLAYER_PROPERTY_SET_RESPONSE;
    }

    @Override
    public void readFromBuffer(ByteBuf buffer) {
        player = buffer.readLong();
        property = Properties.getById(buffer.readByte());
        value = property.readFromBuffer(buffer);
    }

    @Override
    protected void writeToBuffer(ByteBuf buffer) {
        buffer.writeLong(player);
        property.write(value, buffer);
    }

    @Override
    public String toString() {
        return "PlayerPropertySetResponse{" +
                "player=" + player +
                ", property=" + property +
                ", value=" + value +
                '}';
    }
}
