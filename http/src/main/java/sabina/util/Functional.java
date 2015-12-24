package sabina.util;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Map.Entry;
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
        return Stream.of (objs);
    }

    @SafeVarargs static <K, V> Map<K, V> asMap (Entry<K, V>... entries) {
        return streamOf (entries).collect (toMap (Entry::getKey, Entry::getValue));
    }
}
