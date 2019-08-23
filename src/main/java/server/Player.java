package server;

import server.property.Properties;
import server.property.PropertyKey;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Player {
    public final long id;

    private final String username;
    private final Map<PropertyKey, Object> properties;

    public Player() {
        this.id = ThreadLocalRandom.current().nextLong();
        this.username = "Player #" + this.id;

        this.properties = new HashMap<>();
        for (PropertyKey key : Properties.getAllByType(PropertyKey.ROOM_PROPERTY)) {
            this.properties.put(key, key.defaultValue());
        }
    }

    public Player(long id, String username) {
        this.id = id;
        this.username = username;

        properties = new HashMap<>();
        for (PropertyKey key : Properties.getAllByType(PropertyKey.PLAYER_PROPERTY)) {
            properties.put(key, key.defaultValue());
        }
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", properties=" + properties +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setProperty(PropertyKey prop, Object value) {
        this.properties.put(prop, value);
    }

    public Object getProperty(PropertyKey prop) {
        return this.properties.get(prop);
    }
}
