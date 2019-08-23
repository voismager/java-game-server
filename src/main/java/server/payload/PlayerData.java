package server.payload;

import server.property.Properties;
import server.property.PropertyKey;
import server.BufferTools;
import server.Player;
import server.RPlayer;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

public class PlayerData extends Payload {
    public long id;
    public String name;
    public Map<PropertyKey, Object> properties;

    public PlayerData(RPlayer player) {
        this.id = player.player.id;
        this.name = player.player.getUsername();
        this.properties = new HashMap<>();

        for (PropertyKey p : Properties.getAllByType(PropertyKey.PLAYER_PROPERTY)) {
            this.properties.put(p, player.player.getProperty(p));
        }
    }

    public PlayerData(ByteBuf buffer) {
        super(buffer);
    }

    public Player toPlayer() {
        Player player = new Player(id, name);

        for (PropertyKey p : Properties.getAllByType(PropertyKey.PLAYER_PROPERTY)) {
            player.setProperty(p, properties.get(p));
        }

        return player;
    }

    @Override
    public void writeToBuffer(ByteBuf buffer) {
        buffer.writeLong(id);
        BufferTools.writeString(name, buffer);

        buffer.writeInt(properties.size());
        for (Map.Entry<PropertyKey, Object> e : properties.entrySet()) {
            e.getKey().write(e.getValue(), buffer);
        }
    }

    @Override
    public void readFromBuffer(ByteBuf buffer) {
        id = buffer.readLong();
        name = BufferTools.readString(buffer);

        int size = buffer.readInt();
        properties = new HashMap<>();
        for (int i = 0; i < size; i++) {
            PropertyKey property = Properties.getById(buffer.readByte());
            properties.put(property, property.readFromBuffer(buffer));
        }
    }

    @Override
    public String toString() {
        return "PlayerData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", properties=" + properties +
                '}';
    }
}
