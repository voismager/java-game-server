package server.property;

import io.netty.buffer.ByteBuf;

public interface PropertyKey {
    byte PLAYER_PROPERTY = 0;
    byte ROOM_PROPERTY = 1;
    byte SESSION_PROPERTY = 2;

    byte id();

    byte type();

    Object defaultValue();

    Object readFromBuffer(ByteBuf buffer);

    void writeToBuffer(Object value, ByteBuf buffer);

    default void write(Object value, ByteBuf buffer) {
        buffer.writeByte(id());
        writeToBuffer(value, buffer);
    }

    boolean validate(Object value, Object... args);
}
