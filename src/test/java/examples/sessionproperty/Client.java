package examples.sessionproperty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import server.ObjPair;
import server.message.Headers;
import server.message.SessionDataRequest;
import server.message.SessionDataResponse;
import server.property.Properties;
import server.property.PropertyKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        Properties.registerProperty(SuperCoolProperty.KEY);

        ByteBuf buf = Unpooled.buffer(8);
        SessionDataRequest msg0 = new SessionDataRequest();

        buf.writeByte(0); buf.writeByte(1);
        msg0.encode(buf);

        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(InetAddress.getLocalHost(), 7777));

            DataOutputStream toServer = new DataOutputStream(s.getOutputStream());
            toServer.write(buf.array());
            toServer.flush();

            DataInputStream fromServer = new DataInputStream(s.getInputStream());
            byte part0 = fromServer.readByte(); byte part1 = fromServer.readByte();
            int length = (part0 << 1) | part1;
            byte[] b = new byte[length];
            for (int i = 0; i < length; i++) b[i] = fromServer.readByte();

            buf.clear();
            buf.writeBytes(b);

            SessionDataResponse r0 = (SessionDataResponse) Headers.decode(buf);

            for (ObjPair<PropertyKey, Object> p : r0.data.properties) {
                if (p.key == SuperCoolProperty.KEY) {
                    assert p.value.toString().equals("Hello World!");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
