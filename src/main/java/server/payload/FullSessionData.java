package server.payload;

import server.property.Properties;
import server.property.PropertyKey;
import server.BufferTools;
import io.netty.buffer.ByteBuf;
import server.ObjPair;
import server.Session;

import java.util.Arrays;
import java.util.List;

public class FullSessionData extends Payload {
    public String type;
    public ObjPair<PropertyKey, Object>[] properties;
    public PlayerData[] players;
    public RoomData[] rooms;

    public FullSessionData(Session session) {
        this.type = session.type();

        List<PropertyKey> keys = Properties.getAllByType(PropertyKey.SESSION_PROPERTY);

        this.properties = new ObjPair[keys.size()];

        for (int i = 0; i < properties.length; i++) {
            PropertyKey key = keys.get(i);
            Object value = session.getProperty(key);
            this.properties[i] = new ObjPair<>(key, value);
        }

        this.players = session.getRegistered().stream()
                .map(PlayerData::new)
                .toArray(PlayerData[]::new);

        this.rooms = session.getRoomsStream()
                .map(RoomData::new)
                .toArray(RoomData[]::new);
    }

    public FullSessionData(ByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuf buffer) {
        BufferTools.writeString(type, buffer);

        buffer.writeInt(properties.length);
        for (ObjPair<PropertyKey, Object> p : properties)
            p.key.write(p.value, buffer);

        buffer.writeInt(players.length);
        for (PlayerData pl : players)
            pl.writeToBuffer(buffer);

        buffer.writeInt(rooms.length);
        for (RoomData room : rooms)
            room.writeToBuffer(buffer);
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

        players = new PlayerData[buffer.readInt()];
        for (int i = 0; i < players.length; i++)
            players[i] = new PlayerData(buffer);

        rooms = new RoomData[buffer.readInt()];
        for (int i = 0; i < rooms.length; i++)
            rooms[i] = new RoomData(buffer);
    }

    @Override
    public String toString() {
        return "FullSessionData{" +
                "type='" + type + '\'' +
                ", properties=" + Arrays.toString(properties) +
                ", players=" + Arrays.toString(players) +
                ", rooms=" + Arrays.toString(rooms) +
                '}';
    }
}
