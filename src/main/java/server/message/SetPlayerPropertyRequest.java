package server.message;

import server.property.Properties;
import server.property.PropertyKey;
import io.netty.buffer.ByteBuf;

public class SetPlayerPropertyRequest extends Message {
    public PropertyKey prop;
    public Object value;

    public SetPlayerPropertyRequest(PropertyKey prop, Object value) {
        this.prop = prop;
        this.value = value;
    }

    SetPlayerPropertyRequest() {
    }

    @Override
    public Header getHeader() {
        return Headers.SET_PLAYER_PROPERTY_REQUEST;
    }

    @Override
    public void readFromBuffer(ByteBuf buffer) {
        prop = Properties.getById(buffer.readByte());
        value = prop.readFromBuffer(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuf buffer) {
        prop.write(value, buffer);
    }

    @Override
    public String toString() {
        return "SetPlayerPropertyRequest{" +
                "prop=" + prop +
                ", value=" + value +
                '}';
    }
}
