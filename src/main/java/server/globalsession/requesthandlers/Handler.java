package server.globalsession.requesthandlers;

import server.message.ChannelMessage;
import server.message.Header;
import server.Session;

import java.util.function.BiConsumer;

public interface Handler<T extends Session> extends BiConsumer<T, ChannelMessage> {
    Header[] headers();
}
