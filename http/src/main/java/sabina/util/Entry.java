package sabina.util;

import static java.lang.String.format;
import static java.util.Objects.hash;
import static sabina.util.Things.equal;

import java.util.Map;

/**
 * Entry is a key, value association (a pair really, but using pair makes Java orthodox nervous).
 *
 * @author jam
 */
public final class Entry<K, V> implements Map.Entry<K, V> {
    public static <K, V> Entry<K, V> entry (K key, V value) {
        return new Entry<> (key, value);
    }

    public final K key;
    public final V value;

    Entry (K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override public K getKey() { return key; }
    @Override public V getValue() { return value; }
    @Override public V setValue(V value) { throw new UnsupportedOperationException(); }

    @Override public boolean equals (Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Entry that = (Entry)o;
        return
            equal (key, that.key) &&
            equal (value, that.value);
    }

    @Override public int hashCode () {
        return hash (key, value);
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
