package server;

import server.property.Properties;
import server.property.PropertyKey;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class SessionRoom {
    private final int id;
    private final Map<PropertyKey, Object> properties;
    private final Set<RPlayer> members;
    private final GameSession gameSession;

    public SessionRoom(int id, GameSession gameSession) {
        this.id = id;

        this.properties = new HashMap<>();
        for (PropertyKey key : Properties.getAllByType(PropertyKey.ROOM_PROPERTY)) {
            this.properties.put(key, key.defaultValue());
        }

        this.members = new HashSet<>();
        this.gameSession = gameSession;
    }

    public int getId() {
        return id;
    }

    public Object getProperty(PropertyKey prop) {
        return this.properties.get(prop);
    }

    public int getSize() {
        return members.size();
    }

    public Stream<RPlayer> getMembersStream() {
        return members.stream();
    }

    public RPlayer getAnyMember() {
        return members.iterator().next();
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public boolean hasMember(RPlayer player) {
        return this.members.contains(player);
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

    public void setProperty(PropertyKey prop, Object value) {
        this.properties.put(prop, value);
    }

    public void addMember(RPlayer player) {
        this.members.add(player);
    }

    public void removeMember(RPlayer player) {
        this.members.remove(player);
    }

    public void cleanup() {
        members.clear();

        for (PropertyKey r : Properties.getAllByType(PropertyKey.ROOM_PROPERTY))
            properties.put(r, r.defaultValue());
    }
}
