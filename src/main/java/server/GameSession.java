package server;

import server.message.Message;
import server.messagesender.MessageSender;

import java.util.stream.Stream;

public interface GameSession {
    boolean isPlaying();

    boolean isSubscribed(RPlayer player);

    Stream<RPlayer> getSubscribersStream();

    void start(MessageSender sender, RPlayer[] players, Object... args);

    void unsubscribeAll();

    void unsubscribe(RPlayer player);

    void processStopPlayingRequest(RPlayer player);

    void processGameRequest(RPlayer issuer, Message request);
}
