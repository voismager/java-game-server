package server.message;

public interface Header {
    byte id();

    Message getEmptyMessage();
}
