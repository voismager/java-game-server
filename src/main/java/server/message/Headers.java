package server.message;

import com.sun.istack.internal.NotNull;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Supplier;

public enum Headers implements Header {
    SESSION_DATA_REQUEST(SessionDataRequest::new),
    REGISTRATION_REQUEST(RegistrationRequest::new),
    CREATE_ROOM_REQUEST(new Supplier<Message>() {
        final CreateRoomRequest INSTANCE = new CreateRoomRequest();
        public Message get() { return INSTANCE; }
    }),
    JOIN_ROOM_REQUEST(JoinRoomRequest::new),
    LEAVE_ROOM_REQUEST(LeaveRoomRequest::new),
    SET_ROOM_PROPERTY_REQUEST(SetRoomPropertyRequest::new),
    SET_PLAYER_PROPERTY_REQUEST(SetPlayerPropertyRequest::new),
    DISCONNECT_REQUEST(DisconnectRequest::new),
    PING(new Supplier<Message>() {
        final PingMessage INSTANCE = new PingMessage();
        public Message get() { return INSTANCE; }
    }),
    SESSION_DATA_RESPONSE(SessionDataResponse::new),
    REGISTRATION_SUCCEED_RESPONSE(RegistrationSucceedResponse::new),
    PLAYER_CONNECTED_RESPONSE(PlayerConnectedResponse::new),
    PLAYER_DISCONNECTED_RESPONSE(PlayerDisconnectedResponse::new),
    ROOM_CREATED_RESPONSE(RoomCreatedResponse::new),
    ROOM_DESTROYED_RESPONSE(RoomDestroyedResponse::new),
    PLAYER_JOINED_RESPONSE(PlayerJoinedResponse::new),
    PLAYER_LEFT_RESPONSE(PlayerLeftResponse::new),
    ROOM_PROPERTY_SET_RESPONSE(RoomPropertySetResponse::new),
    PLAYER_PROPERTY_SET_RESPONSE(PlayerPropertySetResponse::new),
    PLAYER_UNSUBSCRIBED_RESPONSE(PlayerUnsubscribedResponse::new);

    private static final List<Header> HEADERS;

    static {
        HEADERS = new ArrayList<>(Arrays.asList(values()));
    }

    public static Set<Header> setOf(Collection<Header> headers) {
        return new HeaderSet(headers);
    }

    private static class HeaderSet implements Set<Header> {
        private final Header[] values;
        private final int size;

        HeaderSet(Collection<Header> collection) {
            int length = 0;
            for (Header h : collection) {
                if (h == null) throw new NullPointerException();
                length = h.id() > length ? h.id() : length;
            }
            values = new Header[++length];
            for (Header h : collection) {
                values[h.id()] = h;
            }
            this.size = collection.size();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return size == 0;
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof Header) {
                final int id = ((Header) o).id();
                return values[id] != null;
            }

            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object o : collection) {
                if (!contains(o)) return false;
            }

            return true;
        }

        @Override
        public Iterator<Header> iterator() {
            return new Iterator<Header>() {
                int i = 0;

                @Override
                public boolean hasNext() {
                    Header h;
                    do {
                        h = values[i];
                        if (i+1 == values.length) return false;
                    } while (h == null);
                    return true;
                }

                @Override
                public Header next() {
                    return values[i];
                }
            };
        }

        @Override
        public Object[] toArray() {
            Object[] a = new Object[size];

            int i = 0;
            for (final Iterator<Header> it = iterator(); it.hasNext(); ) {
                a[i++] = it.next();
            }

            return a;
        }

        @Override
        public <T> T[] toArray(@NotNull T[] ts) {
            final T[] a = ts.length >= size ? ts : (T[]) Array.newInstance(ts.getClass().getComponentType(), size);

            int i = 0;
            for (final Iterator<Header> it = iterator(); it.hasNext(); ) {
                a[i++] = (T) it.next();
            }

            return a;
        }

        @Override
        public boolean add(Header header) { throw new UnsupportedOperationException(); }
        @Override
        public boolean remove(Object o) { throw new UnsupportedOperationException(); }
        @Override
        public boolean addAll(Collection<? extends Header> collection) { throw new UnsupportedOperationException(); }
        @Override
        public boolean retainAll(Collection<?> collection) { throw new UnsupportedOperationException(); }
        @Override
        public boolean removeAll(Collection<?> collection) { throw new UnsupportedOperationException(); }
        @Override
        public void clear() { throw new UnsupportedOperationException(); }
    }

    public static Message decode(ByteBuf buffer) {
        byte id = buffer.readByte();
        Header h = HEADERS.get(id);

        Message message = h.getEmptyMessage();
        message.readFromBuffer(buffer);

        return message;
    }

    public static ChannelMessage decode(Channel channel, ByteBuf buffer) {
        return new ChannelMessage(channel, decode(buffer));
    }

    public static synchronized Header registerMessage(Supplier<CustomMessage> emptyMessageGetter) {
        final byte id = (byte) HEADERS.size();

        Header h = new Header() {
            @Override
            public byte id() { return id; }
            @Override
            public CustomMessage getEmptyMessage() {
                CustomMessage m = emptyMessageGetter.get();
                m.setHeader(this);
                return m;
            }
            @Override
            public final int hashCode() { return id; }
            @Override
            public final boolean equals(Object o) { return this == o; }
        };

        HEADERS.add(h);
        return h;
    }

    private final Supplier<Message> getter;

    Headers(Supplier<Message> getter) {
        this.getter = getter;
    }

    @Override
    public byte id() {
        return (byte) ordinal();
    }

    @Override
    public Message getEmptyMessage() {
        return getter.get();
    }
}
