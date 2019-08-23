package server.messagesender;

import server.RPlayer;
import server.message.Message;

import java.util.function.Predicate;

public interface MessageSender {
    MessageSender sendMessage(Message message);

    MessageSender addAll();

    MessageSender addAllExcept(RPlayer p);

    MessageSender addOne(RPlayer p);

    MessageSender addIf(Predicate<RPlayer> predicate);

    MessageSender flush();
}
