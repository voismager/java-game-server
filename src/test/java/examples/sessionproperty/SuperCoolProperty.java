package examples.sessionproperty;

import io.netty.buffer.ByteBuf;
import server.BufferTools;
import server.property.CustomPropertyKey;

public class SuperCoolProperty extends CustomPropertyKey {
    public static final SuperCoolProperty KEY = new SuperCoolProperty();

    @Override
    public byte type() {
        return SESSION_PROPERTY;
    }

    @Override
    public Object defaultValue() {
        return "";
    }

    @Override
    public Object readFromBuffer(ByteBuf buffer) {
        return BufferTools.readString(buffer);
    }

    @Override
    public void writeToBuffer(Object value, ByteBuf buffer) {
        BufferTools.writeString(value.toString(), buffer);
    }

    @Override
    public boolean validate(Object value, Object... args) {
        return true;
    }
}
