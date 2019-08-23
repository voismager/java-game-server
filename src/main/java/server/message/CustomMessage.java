package server.message;

public abstract class CustomMessage extends Message {
    protected Header header;

    public final void setHeader(Header header) {
        this.header = header;
    }
}
