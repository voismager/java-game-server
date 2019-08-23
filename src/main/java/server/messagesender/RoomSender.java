package server.messagesender;

import server.RPlayer;
import server.message.Message;
import io.netty.channel.Channel;
import server.SessionRoom;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class RoomSender implements MessageSender {
    private final List<Channel> channels;
    private final SessionRoom room;

    public RoomSender(SessionRoom room) {
        this.channels = new ArrayList<>();
        this.room = room;
    }

    @Override
    public MessageSender sendMessage(Message message) {
        for (Channel ch : channels) {
            ch.write(message);
        }

        return this;
    }

    @Override
    public MessageSender addAll() {
        room.getMembersStream().forEach(pl -> channels.add(pl.channel));
        return this;
    }

    @Override
    public MessageSender addAllExcept(RPlayer p) {
        room.getMembersStream().forEach(pl -> {
            if (pl.player.id != p.player.id) {
                channels.add(pl.channel);
            }
        });

        return this;
    }

    @Override
    public MessageSender addOne(RPlayer p) {
        channels.add(p.channel);
        return this;
    }

    @Override
    public MessageSender addIf(Predicate<RPlayer> predicate) {
        room.getMembersStream().filter(predicate).forEach(pl -> channels.add(pl.channel));
        return this;
    }

    @Override
    public MessageSender flush() {
        for (Channel ch : channels) {
            ch.flush();
        }

        channels.clear();

        return this;
    }
}
