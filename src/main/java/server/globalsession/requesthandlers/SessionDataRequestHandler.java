package server.globalsession.requesthandlers;

import io.netty.channel.ChannelFutureListener;
import server.Session;
import server.message.ChannelMessage;
import server.message.Header;
import server.message.Headers;
import server.message.SessionDataResponse;
import server.payload.SessionData;

public class SessionDataRequestHandler implements Handler<Session> {
    @Override
    public Header[] headers() {
        return new Header[] { Headers.SESSION_DATA_REQUEST };
    }

    @Override
    public void accept(Session session, ChannelMessage channelMessage) {
        channelMessage.channel.writeAndFlush(new SessionDataResponse(new SessionData(session)))
                .addListener(ChannelFutureListener.CLOSE);
    }
}
