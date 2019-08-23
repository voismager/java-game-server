package server.message;

import io.netty.channel.Channel;

public class ChannelMessage {
    public final Channel channel;
    public final Message request;

    public ChannelMessage(Channel channel, Message request) {
        this.channel = channel;
        this.request = request;
    }

    @Override
    public String toString() {
        return "ChannelMessage{" +
                "channel=" + channel +
                ", request=" + request +
                '}';
    }
}
