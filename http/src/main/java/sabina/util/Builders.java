package sabina.util;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static sabina.util.Checks.checkArgument;
import static sabina.util.Entry.entry;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utility methods to build nested collections using closures and/or varargs
 *
 * @author juanjoaguililla
 */
public final class Builders {
    @SafeVarargs private static <T> void addNotNulls (List<T> list, T... items) {
        list.addAll (asList (items).stream ().filter (item -> item != null).collect (toList ()));
    }

    @SuppressWarnings ("unchecked") @SafeVarargs
    private static <K> void addNotNulls (Map<K, ?> map, Entry<K, ?>... items) {
        checkArgument (map != null);
        checkArgument (items != null);

        Map<K, Object> objectMap = (Map<K, Object>)map;
        asList(items).stream ().forEach (i -> objectMap.put (i.getKey (), i.getValue ()));
    }

    private static <T> T build (Supplier<T> supplier, Consumer<T> builder) {
        checkArgument (supplier != null);
        checkArgument (builder != null);

        T result = supplier.get();
        builder.accept (result);
        return result;
    }

    public static <T> List<T> list (Supplier<List<T>> supplier, Consumer<List<T>> builder) {
        return build (supplier, builder);
    }

    public static <T> List<T> list (Consumer<List<T>> builder) {
        return list (ArrayList::new, builder);
    }

    @SafeVarargs public static <T> List<T> list (Supplier<List<T>> supplier, T... items) {
        return list (supplier, l -> addNotNulls (l, items));
    }

    @SafeVarargs public static <T> List<T> list (T... items) {
        return list (ArrayList::new, l -> addNotNulls (l, items));
    }

    public static <K> Map<K, ?> map (Supplier<Map<K, ?>> supplier, Consumer<Map<K, ?>> builder) {
        return build (supplier, builder);
    }

    public static <K> Map<K, ?> map (Consumer<Map<K, ?>> builder) {
        return map (LinkedHashMap::new, builder);
    }

    public static <K> Map<K, ?> map (Supplier<Map<K, ?>> supplier, Entry<K, ?>... items) {
        return map (supplier, m -> addNotNulls (m, items));
    }

    public static <K> Map<K, ?> map (Entry<K, ?>... items) {
        return map(LinkedHashMap::new, m -> addNotNulls (m, items));
    }

    public static <E, T> Entry<E, List<T>> listEntry (
        E key, Supplier<List<T>> supplier, Consumer<List<T>> builder) {

        return entry (key, build (supplier, builder));
    }

    public static <E, T> Entry<E, List<T>> listEntry (E key, Consumer<List<T>> builder) {
        return entry (key, list (builder));
    }

    @SafeVarargs
    public static <E, T> Entry<E, List<T>> listEntry (E key, Supplier<List<T>> supplier, T... items) {
        return entry (key, list (supplier, items));
    }

    @SafeVarargs public static <E, T> Entry<E, List<T>> listEntry (E key, T... items) {
        return entry (key, list (items));
    }

    public static <E, T> Entry<E, List<T>> listEntry (E key, List<T> items) {
        return entry (key, items);
    }

    public static <E, K> Entry<E, Map<K, ?>> mapEntry (
        E key, Supplier<Map<K, ?>> supplier, Consumer<Map<K, ?>> builder) {

        return entry (key, build (supplier, builder));
    }

    public static <E, K> Entry<E, Map<K, ?>> mapEntry (E key, Consumer<Map<K, ?>> builder) {
        return entry (key, map (builder));
    }

    public static <E, K> Entry<E, Map<K, ?>> mapEntry (
        E key, Supplier<Map<K, ?>> supplier, Entry<K, ?>... items) {

        return entry (key, map(supplier, items));
    }

    public static <E, K> Entry<E, Map<K, ?>> mapEntry (E key, Entry<K, ?>... items) {
        return entry (key, map(items));
    }

    public static <E, K> Entry<E, Map<K, ?>> mapEntry (E key, Map<K, ?> items) {
        return entry (key, items);
    }

    public static <T> T get (List<?> collection, Object... keys) {
        return getValue (collection, keys);
    }

    public static <T> T get (Map<?, ?> collection, Object... keys) {
        return getValue (collection, keys);
    }

    private static <T> T getValue (Object collection, Object... keys) {
        Object pointer = collection;
        for (Object key : keys)
            if (pointer instanceof List && key instanceof Integer)
                pointer = ((List)pointer).get ((Integer)key);
            else if (pointer instanceof Map)
                pointer = ((Map)pointer).get (key);
            else
                throw new IllegalArgumentException ();

        @SuppressWarnings ("unchecked") T result = (T)pointer;
        return result;
    }
}
