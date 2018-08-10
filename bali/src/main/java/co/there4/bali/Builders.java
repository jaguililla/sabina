package co.there4.bali;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Utility methods to build nested collections using closures and/or varargs
 */
public interface Builders {
    @SafeVarargs static <T> void addNotNulls (Collection<T> list, T... items) {
        list.addAll (items == null?
            new ArrayList<> () :
            stream (items)
                .filter (Objects::nonNull)
                .collect (toList ())
        );
    }

    @SafeVarargs @SuppressWarnings ("unchecked")
    static <K> void addNotNulls (Map<K, ?> map, Entry<K, ?>... items) {
        if (items != null) {
            Map<K, Object> objectMap = (Map<K, Object>)map;
            stream (items)
                .filter (Objects::nonNull)
                .forEach (i -> objectMap.put (i.getKey (), i.getValue ()));
        }
    }

    @SuppressWarnings ("unchecked")
    static <K> void addNotNulls (Map<K, ?> map, Stream<Entry<K, ?>> items) {
        if (items != null) {
            Map<K, Object> objectMap = (Map<K, Object>)map;
            items
                .filter (Objects::nonNull)
                .forEach (i -> objectMap.put (i.getKey (), i.getValue ()));
        }
    }

    static <T> T build (Supplier<T> supplier, Consumer<T> builder) {
        Checks.require (builder != null);

        T result = supplier.get();
        builder.accept (result);
        return result;
    }

    @SafeVarargs static <T> Set<T> setOf (T... items) {
        return build (LinkedHashSet::new, l -> addNotNulls (l, items));
    }

    @SafeVarargs static <T> List<T> listOf (T... items) {
        return build (ArrayList::new, l -> addNotNulls (l, items));
    }

    @SafeVarargs static <K> Map<K, ?> mapOf (Entry<K, ?>... items) { // NOSONAR Remove wildcard type
        return build (LinkedHashMap::new, m -> addNotNulls (m, items));
    }

    static <K> Map<K, ?> mapOf (Stream<Entry<K, ?>> items) { // NOSONAR Remove wildcard type
        return build (LinkedHashMap::new, m -> addNotNulls (m, items));
    }

    @SafeVarargs static <K, V> Map<K, V> typedMapOf (Entry<K, V>... items) {
        return build (LinkedHashMap::new, m -> addNotNulls (m, items));
    }

    static <K, V> Entry<K, V> entry (K key, V value) {
        return new SimpleImmutableEntry<> (key, value);
    }

    static <T> T get (Collection<?> collection, Object... keys) {
        return getValue (collection, keys);
    }

    static <T> T get (Map<?, ?> collection, Object... keys) {
        return getValue (collection, keys);
    }

    static <T> T getValue (Object collection, Object... keys) {
        Object pointer = collection;

        for (Object key : keys)
            if (pointer instanceof List && key instanceof Integer) {
                List<?> list = (List)pointer;
                Integer index = (Integer)key;

                if (index < 0 || index > (list.size () - 1))
                    throw new IllegalArgumentException ("'" + key + "' not found");

                pointer = list.get (index);
            }
            else if (pointer instanceof Map) {
                Map<?, ?> map = (Map)pointer;

                if (!map.containsKey (key))
                    throw new IllegalArgumentException ("'" + key + "' not found");

                pointer = map.get (key);
            }
            else {
                throw new IllegalArgumentException ("Error with '" + key + "' key");
            }

        @SuppressWarnings ("unchecked") T result = (T)pointer;
        return result;
    }
}
