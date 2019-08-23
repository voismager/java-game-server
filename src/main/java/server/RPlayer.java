package server;

import io.netty.channel.Channel;

import java.util.Objects;

public class RPlayer {
    public final Channel channel;
    public final Player player;

    public RPlayer(Channel channel, Player player) {
        this.channel = channel;
        this.player = player;
    }

    @Override
    public int hashCode() {
        return player.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RPlayer player1 = (RPlayer) o;
        return Objects.equals(player, player1.player);
    }
}
