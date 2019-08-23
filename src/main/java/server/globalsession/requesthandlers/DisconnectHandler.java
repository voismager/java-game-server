package server.globalsession.requesthandlers;

import server.property.Properties;
import server.RPlayer;
import server.message.*;
import server.SessionRoom;
import server.globalsession.GlobalSession;

public class DisconnectHandler implements Handler<GlobalSession> {
    @Override
    public void accept(GlobalSession session, ChannelMessage message) {
        final RPlayer player = session.getRegistered(message.channel);
        final SessionRoom room = session.getRoomByPlayer(player);

        if (room != null) {
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

        session.unregister(player, ((DisconnectRequest) message.request).cause );
    }

    @Override
    public Header[] headers() {
        return new Header[] { Headers.DISCONNECT_REQUEST };
    }
}
