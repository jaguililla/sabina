package co.there4.bali;

import static co.there4.bali.Builders.entry;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map.Entry;

/**
 * Utilities to call HTTP methods over a default HTTP client.
 */
public interface Http { // NOSONAR Is OK for this interface to hold constants

    static List<Entry<String, String>> parseQueryString (final String queryString) {
        final String[] queryParameters = queryString.split ("&");

        return stream (queryParameters)
            .map (it -> it.split ("="))
            .map (it -> entry (it[0].trim (), it.length == 1? "" : it[1].trim ()))
            .collect (toList());
    }

    static String encode (final String path) {
        return Unchecked.get(() -> URLEncoder.encode (path, UTF_8.name ()));
    }
}
