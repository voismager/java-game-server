package examples.sessionproperty;

import server.GameSession;
import server.Player;
import server.RPlayer;
import server.globalsession.GlobalSession;
import server.globalsession.requesthandlers.SessionDataRequestHandler;
import server.message.Message;
import server.messagesender.MessageSender;
import server.property.Properties;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.stream.Stream;

public class ExampleSession extends GlobalSession {
    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        Properties.registerProperty(SuperCoolProperty.KEY);
        ExampleSession example = new ExampleSession();
        example.start(new InetSocketAddress(InetAddress.getLocalHost(), 7777));
    }

    public ExampleSession() {
        super(
                id -> new Player(id, "Player #" + id),
                roomId -> new GameSession() {
                    @Override
                    public boolean isPlaying() { return false; }
                    @Override
                    public boolean isSubscribed(RPlayer player) { return false; }
                    @Override
                    public Stream<RPlayer> getSubscribersStream() { return Stream.empty(); }
                    @Override
                    public void start(MessageSender sender, RPlayer[] players, Object... args) { }
                    @Override
                    public void unsubscribeAll() { }
                    @Override
                    public void unsubscribe(RPlayer player) { }
                    @Override
                    public void processStopPlayingRequest(RPlayer player) { }
                    @Override
                    public void processGameRequest(RPlayer issuer, Message request) { }
                },
                new SessionDataRequestHandler()
        );

        setProperty(SuperCoolProperty.KEY, "Hello World!");
    }
}
