package server.globalsession.requesthandlers;

import server.property.Properties;
import server.RPlayer;
import server.message.*;
import server.SessionRoom;
import server.globalsession.GlobalSession;

public class CreateRoomHandler implements Handler<GlobalSession> {
    @Override
    public Header[] headers() {
        return new Header[] { Headers.CREATE_ROOM_REQUEST };
    }

    @Override
    public void accept(GlobalSession session, ChannelMessage req) {
        final RPlayer player = session.getRegistered(req.channel);

        SessionRoom room = session.createRoom();

        room.addMember(player);
        room.setProperty(Properties.ROOM_HOST, player.player.id);

        session.getSessionSender()
                .addAll()
                .sendMessage(new PlayerJoinedResponse(player.player.id, room.getId()))
                .sendMessage(new RoomPropertySetResponse(room.getId(), Properties.ROOM_HOST, player.player.id))
                .flush();
    }
}
