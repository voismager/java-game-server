package server.message;

import server.property.Properties;
import server.property.PropertyKey;
import io.netty.buffer.ByteBuf;

public class RoomPropertySetResponse extends Message {
    public int room;
    public PropertyKey property;
    public Object value;

    public RoomPropertySetResponse(int room, PropertyKey property, Object value) {
        this.room = room;
        this.property = property;
        this.value = value;
    }

    RoomPropertySetResponse() {
    }

    @Override
    public Header getHeader() {
        return Headers.ROOM_PROPERTY_SET_RESPONSE;
    }

    @Override
    public void readFromBuffer(ByteBuf buffer) {
        room = buffer.readInt();
        property = Properties.getById(buffer.readByte());
        value = property.readFromBuffer(buffer);
    }

    @Override
    protected void writeToBuffer(ByteBuf buffer) {
        buffer.writeInt(room);
        property.write(value, buffer);
    }

    @Override
    public String toString() {
        return "RoomPropertySetResponse{" +
                "room=" + room +
                ", property=" + property +
                ", value=" + value +
                '}';
    }
}
