package server.messagesender;

import server.RPlayer;
import server.message.Message;
import io.netty.channel.Channel;
import server.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SessionSender implements MessageSender {
    private final List<Channel> channels;
    private final Session session;

    public SessionSender(Session session) {
        this.channels = new ArrayList<>();
        this.session = session;
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
        for (RPlayer player : session.getRegistered()) {
            channels.add(player.channel);
        }

        return this;
    }

    @Override
    public MessageSender addAllExcept(RPlayer p) {
        for (RPlayer player : session.getRegistered()) {
            if (player.player.id != p.player.id) {
                channels.add(player.channel);
            }
        }

        return this;
    }

    @Override
    public MessageSender addOne(RPlayer p) {
        channels.add(p.channel);

        return this;
    }

    @Override
    public MessageSender addIf(Predicate<RPlayer> predicate) {
        for (RPlayer player : session.getRegistered()) {
            if (predicate.test(player)) {
                channels.add(player.channel);
            }
        }

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
