package server.property;

public abstract class CustomPropertyKey implements PropertyKey {
    private byte id;

    public void setId(byte id) {
        this.id = id;
    }

    @Override
    public byte id() {
        return id;
    }
}
