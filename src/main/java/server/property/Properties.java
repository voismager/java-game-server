package server.property;

import io.netty.buffer.ByteBuf;
import server.BufferTools;
import server.SessionRoom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Properties implements PropertyKey {
    PLAYER_READY_FLAG {
        @Override
        public byte type() {
            return PLAYER_PROPERTY;
        }

        @Override
        public Object defaultValue() {
            return false;
        }

        @Override
        public Object readFromBuffer(ByteBuf buffer) {
            return buffer.readBoolean();
        }

        @Override
        public void writeToBuffer(Object value, ByteBuf buffer) {
            buffer.writeBoolean((Boolean) value);
        }

        @Override
        public boolean validate(Object value, Object... args) {
            return true;
        }
    },

    ROOM_SECRET {
        @Override
        public byte type() {
            return ROOM_PROPERTY;
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
            BufferTools.writeString((String)value, buffer);
        }

        @Override
        public boolean validate(Object value, Object... args) {
            return true;
        }
    },

    ROOM_HOST {
        @Override
        public byte type() {
            return ROOM_PROPERTY;
        }

        @Override
        public Object defaultValue() {
            return null;
        }

        @Override
        public Object readFromBuffer(ByteBuf buffer) {
            return buffer.readLong();
        }

        @Override
        public void writeToBuffer(Object value, ByteBuf buffer) {
            buffer.writeLong((Long)value);
        }

        @Override
        public boolean validate(Object value, Object... args) {
            SessionRoom room = (SessionRoom) args[1];
            return room.getMembersStream().anyMatch(player -> player.player.id == (Long) value);
        }
    };

    private static final List<PropertyKey> PROPERTY_KEYS;

    static {
        PROPERTY_KEYS = new ArrayList<>(Arrays.asList(values()));
    }

    public static synchronized byte registerProperty(CustomPropertyKey key) {
        final byte id = (byte) PROPERTY_KEYS.size();
        key.setId(id);
        PROPERTY_KEYS.add(key);
        return id;
    }

    public static List<PropertyKey> getAll() {
        return PROPERTY_KEYS;
    }

    public static List<PropertyKey> getAllByType(byte type) {
        List<PropertyKey> keys = new ArrayList<>();

        for (PropertyKey k : PROPERTY_KEYS) {
            if (k.type() == type) keys.add(k);
        }

        return keys;
    }

    public static PropertyKey getById(byte id) {
        return PROPERTY_KEYS.get(id);
    }

    @Override
    public byte id() {
        return (byte) ordinal();
    }
}
