package sabina.util;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Utility methods to build nested collections using closures and/or varargs
 *
 * @author juanjoaguililla
 */
public final class Builders {
    @SafeVarargs private static <T> void addNotNulls (List<T> list, T... items) {
        list.addAll (asList (items).stream ().filter (item -> item != null).collect (toList()));
    }

    private static <K> void addNotNulls (Map<K, ?> map, Entry<K, ?>... items) {
        Map<K, Object> objectMap = (Map<K, Object>)map;
        asList(items).stream ().forEach (i -> objectMap.put (i.getKey (), i.getValue ()));
    }

    public static <T> T build (Supplier<T> supplier, Consumer<T> builder) {
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

    public static <K, V> Entry<K, V> entry (K key, V value) {
        return new Entry<> (key, value);
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

    public static <T> Collection<T> flatten (Collection<T> collection) {
        throw new UnsupportedOperationException ();
    }

    public static <T> Collection<T> merge (Collection<T> a, Collection<T> b) {
        throw new UnsupportedOperationException ();
    }

//    public static void main (String [] args) {
//        list (LinkedList::new, l -> {
//            l.add("");
//            l.add("");
//        });
//
//        list (l -> {
//            l.add("");
//            l.add("");
//        });
//
//        List<?> lst = list (
//            "",
//            "",
//            false
//        );
//
//        Map<?, ?> m1 = new LinkedHashMap<> ();
//        List<LocalTime> l1 = new ArrayList<> ();
//
//        Map<?, ?> m = map (
//            mapEntry ("m1", m1),
//            listEntry ("dt", l1),
//            mapEntry ("m",
//                listEntry ("m1", true, false)
//            ),
//            listEntry ("lst", l -> {
//                l.add (0);
//                l.add ("");
//            }),
//            entry ("", list ("", ""))
//        );
//
//        Map<?, ?> person = map (
//            entry ("name", "Juanjo"),
//            entry ("surname", "Aguililla"),
//            entry ("birth", LocalDate.of (1979, 1, 22)),
//            mapEntry ("address",
//                entry ("street", "C/Albufera")
//            ),
//            listEntry ("sports",
//                mapEntry ("running",
//                    entry ("active", false)
//                ),
//                mapEntry ("basket",
//                    entry ("active", true)
//                ),
//                mapEntry ("karate",
//                    entry ("active", true)
//                )
//            )
//        );
//
//        System.out.println (((List<?>)m.get ("lst")).get(0));
//        int intVal = get (m, "lst", 0);
//        System.out.println (intVal);
//        System.out.println ((Integer)get (m, "lst", 0));
//        System.out.println (((List<?>)((Map<?, ?>)m.get ("m")).get ("m1")).get(0));
//        boolean boolVal = get (lst, 2);
//        String strVal = (get (lst, 2)).toString ();
//        System.out.println (boolVal);
//    }
}
