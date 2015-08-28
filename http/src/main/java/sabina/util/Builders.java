package sabina.util;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static sabina.util.Checks.checkArgument;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utility methods to build nested collections using closures and/or varargs
 *
 * @author juanjoaguililla
 */
public final class Builders {
    private static void addNotNulls (Collection<Object>list, Object... items) {
        list.addAll (items == null? new ArrayList () : stream (items)
            .filter (it -> it != null)
            .collect (toList ())
        );
    }

    @SafeVarargs
    private static void addNotNulls (Map<Object, Object> map, Entry<Object, Object>... items) {
        if (items != null)
            stream (items)
                .filter (it -> it != null)
                .forEach (it -> map.put (it.getKey (), it.getValue ()));
    }

    private static <T> T build (Supplier<T> supplier, Consumer<T> builder) {
        checkArgument (builder != null);

        T result = supplier.get();
        builder.accept (result);
        return result;
    }

    public static Set<Object> set (Object... items) {
        return build (LinkedHashSet::new, l -> addNotNulls (l, items));
    }

    public static List<Object> list (Object... items) {
        return build (ArrayList::new, l -> addNotNulls (l, items));
    }

    @SafeVarargs public static Map<Object, Object> map (Entry<Object, Object>... items) {
        return build (LinkedHashMap::new, m -> addNotNulls (m, items));
    }

    public static Object get (Collection<Object> collection, Object... keys) {
        return getValue (collection, keys);
    }

    public static Object get (Map<Object, Object> collection, Object... keys) {
        return getValue (collection, keys);
    }

    private static Object getValue (Object collection, Object... keys) {
        Object pointer = collection;

        for (Object key : keys)
            if (pointer instanceof List && key instanceof Integer) {
                List list = (List)pointer;
                Integer index = (Integer)key;

                if (index < 0 || index > (list.size () - 1))
                    throw new IllegalArgumentException ();

                pointer = list.get (index);
            }
            else if (pointer instanceof Map) {
                Map map = (Map)pointer;

                if (!map.containsKey (key))
                    throw new IllegalArgumentException ();

                pointer = map.get (key);
            }
            else {
                throw new IllegalArgumentException ();
            }

        return pointer;
    }

    static void _create () { new Builders (); }

    private Builders () {
        throw new IllegalStateException ();
    }
}
