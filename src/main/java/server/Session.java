package server;

import server.message.Header;
import server.property.PropertyKey;
import server.message.ChannelMessage;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.stream.Stream;

public interface Session {
    String type();

    Collection<Header> unregisteredHeaders();

    void start(InetSocketAddress address) throws InterruptedException;

    void stop() throws InterruptedException;

    void addRequest(ChannelMessage message);

    void setProperty(PropertyKey property, Object value);

    Object getProperty(PropertyKey property);

    Collection<RPlayer> getRegistered();

    Stream<SessionRoom> getRoomsStream();
}
