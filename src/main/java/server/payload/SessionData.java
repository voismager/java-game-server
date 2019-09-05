package server.payload;

import io.netty.buffer.ByteBuf;
import server.BufferTools;
import server.ObjPair;
import server.Session;
import server.property.Properties;
import server.property.PropertyKey;

import java.util.Arrays;
import java.util.List;

public class SessionData extends Payload {
    public String type;
    public ObjPair<PropertyKey, Object>[] properties;

    public SessionData(Session session) {
        this.type = session.type();

        List<PropertyKey> keys = Properties.getAllByType(PropertyKey.SESSION_PROPERTY);

        this.properties = new ObjPair[keys.size()];

        for (int i = 0; i < properties.length; i++) {
            PropertyKey key = keys.get(i);
            Object value = session.getProperty(key);
            this.properties[i] = new ObjPair<>(key, value);
        }
    }

    public SessionData(ByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuf buffer) {
        BufferTools.writeString(type, buffer);

        buffer.writeInt(properties.length);
        for (ObjPair<PropertyKey, Object> p : properties)
            p.key.write(p.value, buffer);
    }

    @Override
    public void readFromBuffer(ByteBuf buffer) {
        type = BufferTools.readString(buffer);

        properties = new ObjPair[buffer.readInt()];
        for (int i = 0; i < properties.length; i++) {
            PropertyKey key = Properties.getById(buffer.readByte());
            Object value = key.readFromBuffer(buffer);
            properties[i] = new ObjPair<>(key, value);
        }
    }

    @Override
    public String toString() {
        return "SessionData{" +
                "type='" + type + '\'' +
                ", properties=" + Arrays.toString(properties) +
                '}';
    }
}
