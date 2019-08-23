package server.payload;

import server.property.Properties;
import server.property.PropertyKey;
import server.BufferTools;
import io.netty.buffer.ByteBuf;
import server.SessionRoom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RoomData extends Payload {
    public int id;
    public long[] members;
    public long[] players;
    public Map<PropertyKey, Object> properties;

    public RoomData(SessionRoom room) {
        this.id = room.getId();

        this.properties = new HashMap<>();

        for (PropertyKey p : Properties.getAllByType(PropertyKey.ROOM_PROPERTY))
            this.properties.put(p, room.getProperty(p));

        this.members = room.getMembersStream()
                .mapToLong(p -> p.player.id)
                .toArray();

        this.players = room.getGameSession().getSubscribersStream()
                .mapToLong(p -> p.player.id)
                .toArray();
    }

    public RoomData(ByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuf buffer) {
        buffer.writeInt(id);
        BufferTools.writeLongArray(members, buffer);
        BufferTools.writeLongArray(players, buffer);
        BufferTools.writeProperties(properties, buffer);
    }

    @Override
    public void readFromBuffer(ByteBuf buffer) {
        id = buffer.readInt();
        members = BufferTools.readLongArray(buffer);
        players = BufferTools.readLongArray(buffer);
        properties = BufferTools.readProperties(buffer);
    }

    @Override
    public String toString() {
        return "RoomData{" +
                "id=" + id +
                ", members=" + Arrays.toString(members) +
                ", players=" + Arrays.toString(players) +
                ", properties=" + properties +
                '}';
    }
}
