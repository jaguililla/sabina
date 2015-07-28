package sabina.util;

import static java.lang.String.format;

import java.util.Map;

/**
 * Created by jam on 7/18/15.
 */
public final class Entry<K, V> implements Map.Entry<K, V> {
    public final K key;
    public final V value;

    Entry (K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override public K getKey() { return key; }
    @Override public V getValue() { return value; }
    @Override public V setValue(V value) { throw new UnsupportedOperationException(); }

    @Override public boolean equals (Object obj) {
//        return Objects.equal (key, obj.key);
        return false;
    }

    @Override public int hashCode () {
        return Objects.hash (key, value);
    }

    @Override public String toString () {
        return format ("(%s, %s)", key, value);
    }

    public Entry<K, V> key (K key) {
        return new Entry<> (key, value);
    }

    public Entry<K, V> value (V value) {
        return new Entry<> (key, value);
    }
}
