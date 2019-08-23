package server.globalsession.requesthandlers;

import server.RPlayer;
import server.message.*;
import server.SessionRoom;
import server.globalsession.GlobalSession;

public class JoinRoomHandler implements Handler<GlobalSession> {
    @Override
    public Header[] headers() {
        return new Header[] { Headers.JOIN_ROOM_REQUEST };
    }

    @Override
    public void accept(GlobalSession session, ChannelMessage req) {
        final RPlayer player = session.getRegistered(req.channel);
        final JoinRoomRequest request = (JoinRoomRequest) req.request;

        SessionRoom room = session.getRoomById(request.roomId);

        if (room != null && session.getRoomByPlayer(player) == null) {
            room.addMember(player);

            session.getSessionSender()
                    .addAll()
                    .sendMessage(new PlayerJoinedResponse(player.player.id, room.getId()))
                    .flush();
        }
    }
}
