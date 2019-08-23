package server;

public class ObjPair<K, V> {
    public K key;
    public V value;

    public ObjPair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "ObjPair{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
