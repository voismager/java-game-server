package server.messagesender;

import server.RPlayer;
import server.message.Message;
import io.netty.channel.Channel;
import server.GameSession;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class GameSender implements MessageSender {
    private final List<Channel> channels;
    private final GameSession session;

    public GameSender(GameSession session) {
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
        session.getSubscribersStream().forEach(pl -> channels.add(pl.channel));
        return this;
    }

    @Override
    public MessageSender addAllExcept(RPlayer p) {
        session.getSubscribersStream().forEach(pl -> {
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
        session.getSubscribersStream().filter(predicate).forEach(pl -> channels.add(pl.channel));
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
