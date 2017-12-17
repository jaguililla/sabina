package sabina.util;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Method aliases to work with Java 8 functional features.
 *
 * @author jam
 */
public interface Functional {
    static <T> Optional<T> some (T obj) {
        return Optional.ofNullable (obj);
    }

    @SafeVarargs static <T> Stream<T> streamOf (T... objs) {
        return objs == null? Stream.of () : Stream.of (objs);
    }

    @SafeVarargs static <K, V> Map<K, V> asTMap (Entry<K, V>... entries) {
        return streamOf (entries)
            .filter (Objects::nonNull)
            .collect (toMap (Entry::getKey, Entry::getValue));
    }

    static Map<?, ?> asMap (Entry<?, ?>... entries) {
        return streamOf (entries)
            .filter (Objects::nonNull)
            .collect (toMap (Entry::getKey, Entry::getValue));
    }
}
