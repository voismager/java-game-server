package server;

public class ShortMessageCodes {
    public static final byte REQUEST = 0;
    public static final byte TIMEOUT = 1;
    public static final byte DESYNC = 2;
    public static final byte SERVER = 3;

    public static String toString(byte b) {
        switch (b) {
            case REQUEST: return "REQUEST";
            case TIMEOUT: return "TIMEOUT";
            case DESYNC: return "DESYNC";
            case SERVER: return "SERVER";
        }

        return "UNKNOWN";
    }
}
