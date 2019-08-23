package server.globalsession.requesthandlers;

import server.RPlayer;
import server.message.ChannelMessage;
import server.message.Header;
import server.message.Headers;
import server.message.RegistrationRequest;
import server.globalsession.GlobalSession;

public class RegistrationHandler implements Handler<GlobalSession> {
    @Override
    public Header[] headers() {
        return new Header[] { Headers.REGISTRATION_REQUEST };
    }

    @Override
    public void accept(GlobalSession session, ChannelMessage request) {
        final RegistrationRequest registerRequest = (RegistrationRequest) request.request;

        session.register(new RPlayer(request.channel, session.getPlayerInformation(registerRequest.id)));
    }
}
