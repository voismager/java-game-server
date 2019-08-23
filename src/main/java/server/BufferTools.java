package server;

import server.property.Properties;
import server.property.PropertyKey;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

import java.util.*;

public final class BufferTools {
    private BufferTools() {
    }

    public static void writeProperties(Map<PropertyKey, Object> props, ByteBuf buf) {
        buf.writeInt(props.size());

        for (Map.Entry<PropertyKey, Object> e : props.entrySet()) {
            e.getKey().write(e.getValue(), buf);
        }
    }

    public static Map<PropertyKey, Object> readProperties(ByteBuf buf) {
        int size = buf.readInt();

        Map<PropertyKey, Object> properties = new HashMap<>();

        for (int i = 0; i < size; i++) {
            PropertyKey property = Properties.getById(buf.readByte());
            properties.put(property, property.readFromBuffer(buf));
        }

        return properties;
    }

    public static void writeUUID(UUID uuid, ByteBuf buf) {
        buf.ensureWritable(Long.BYTES * 2)
                .writeLong(uuid.getMostSignificantBits())
                .writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(ByteBuf buf) {
        long most = buf.readLong();
        long least = buf.readLong();
        return new UUID(most, least);
    }

    public static void writeString(String value, ByteBuf buf) {
        byte[] bytes = value.getBytes(CharsetUtil.UTF_8);
        buf.writeInt(value.length()).writeBytes(bytes);
    }

    public static String readString(ByteBuf buf) {
        int length = buf.readInt();
        return buf.readCharSequence(length, CharsetUtil.UTF_8).toString();
    }

    public static void writeUUIDList(List<UUID> value, ByteBuf buf) {
        int length = value.size();

        buf.writeInt(length);

        for (int i = 0; i < length; i++)
            writeUUID(value.get(i), buf);
    }

    public static List<UUID> readUUIDList(ByteBuf buf) {
        int length = buf.readInt();

        List<UUID> list = new ArrayList<>(length);

        for (int i = 0; i < length; i++)
            list.add(readUUID(buf));

        return list;
    }

    public static void writeUUIDArray(UUID[] value, ByteBuf buf) {
        int length = value.length;

        buf.writeInt(length);

        for (int i = 0; i < length; i++)
            writeUUID(value[i], buf);
    }

    public static UUID[] readUUIDArray(ByteBuf buf) {
        int length = buf.readInt();

        UUID[] array = new UUID[length];

        for (int i = 0; i < length; i++)
            array[i] = readUUID(buf);

        return array;
    }

    public static void writeShortArray(short[] value, ByteBuf buffer) {
        int length = value.length;

        buffer.writeInt(length);

        for (int i = 0; i < length; i++)
            buffer.writeShort(value[i]);
    }

    public static short[] readShortArray(ByteBuf buffer) {
        int length = buffer.readInt();

        short[] array = new short[length];

        for (int i = 0; i < length; i++)
            array[i] = buffer.readShort();

        return array;
    }

    public static void writeIntArray(int[] value, ByteBuf buffer) {
        int length = value.length;

        buffer.writeInt(length);

        for (int i = 0; i < length; i++)
            buffer.writeInt(value[i]);
    }

    public static int[] readIntArray(ByteBuf buffer) {
        int length = buffer.readInt();

        int[] array = new int[length];

        for (int i = 0; i < length; i++)
            array[i] = buffer.readInt();

        return array;
    }

    public static void writeLongArray(long[] value, ByteBuf buffer) {
        int length = value.length;

        buffer.writeInt(length);

        for (int i = 0; i < length; i++)
            buffer.writeLong(value[i]);
    }

    public static long[] readLongArray(ByteBuf buffer) {
        int length = buffer.readInt();

        long[] array = new long[length];

        for (int i = 0; i < length; i++)
            array[i] = buffer.readLong();

        return array;
    }

    public static void writeStringArray(String[] value, ByteBuf buf) {
        int length = value.length;

        buf.writeInt(length);

        for (int i = 0; i < length; i++)
            writeString(value[i], buf);
    }

    public static String[] readStringArray(ByteBuf buf) {
        int length = buf.readInt();

        String[] array = new String[length];

        for (int i = 0; i < length; i++)
            array[i] = readString(buf);

        return array;
    }
}
