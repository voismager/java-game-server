package server.globalsession.requesthandlers;

import server.RPlayer;
import server.message.*;
import server.globalsession.GlobalSession;

public class SetPlayerPropertyHandler implements Handler<GlobalSession> {
    @Override
    public void accept(GlobalSession session, ChannelMessage req) {
        final RPlayer player = session.getRegistered(req.channel);
        final SetPlayerPropertyRequest request = (SetPlayerPropertyRequest) req.request;

        if (request.value != player.player.getProperty(request.prop)) {
            if (request.prop.validate(request.value, session, session.getRoomByPlayer(player), player)) {
                player.player.setProperty(request.prop, request.value);

                session.getSessionSender()
                        .addAll()
                        .sendMessage(new PlayerPropertySetResponse(player.player.id, request.prop, request.value))
                        .flush();
            }
        }
    }

    @Override
    public Header[] headers() {
        return new Header[] { Headers.SET_PLAYER_PROPERTY_REQUEST };
    }
}
