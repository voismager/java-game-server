package server.globalsession.requesthandlers;

import server.property.Properties;
import server.RPlayer;
import server.message.*;
import server.SessionRoom;
import server.globalsession.GlobalSession;

public class LeaveRoomHandler implements Handler<GlobalSession> {
    @Override
    public Header[] headers() {
        return new Header[] { Headers.LEAVE_ROOM_REQUEST };
    }

    @Override
    public void accept(GlobalSession session, ChannelMessage req) {
        final RPlayer player = session.getRegistered(req.channel);
        final LeaveRoomRequest request = (LeaveRoomRequest) req.request;

        SessionRoom room = session.getRoomById(request.roomId);

        if (session.getRoomByPlayer(player) == room) {
            if (room.getGameSession().isSubscribed(player)) {
                room.getGameSession().processStopPlayingRequest(player);
            }

            room.removeMember(player);

            session.getSessionSender()
                    .addAll()
                    .sendMessage(new PlayerLeftResponse(player.player.id, room.getId()))
                    .flush();

            if (room.isEmpty()) {
                session.destroyRoom(room.getId());

            } else if (room.getProperty(Properties.ROOM_HOST).equals(player.player.id)) {
                final RPlayer nextHost = room.getAnyMember();

                room.setProperty(Properties.ROOM_HOST, nextHost.player.id);

                session.getSessionSender()
                        .addAll()
                        .sendMessage(new RoomPropertySetResponse(room.getId(), Properties.ROOM_HOST, nextHost.player.id))
                        .flush();
            }
        }
    }
}
