package co.there4.bali;

import static co.there4.bali.Checks.requireNotEmpty;
import static co.there4.bali.Checks.requireNotNull;
import static java.util.stream.Collectors.joining;
import static co.there4.bali.Things.stringOf;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Several string utilities not included in the JDK. Some are in other libraries like Guava or
 * Commons Lang.
 *
 * <p>I implement them because the previously mentioned libraries just duplicate some functions
 * with modern JREs.
 *
 * @author jam
 */
public interface Strings {
    /** Variable prefix for string filtering. */
    String VARIABLE_PREFIX = "${";
    /** Variable sufix for string filtering. */
    String VARIABLE_SUFFIX = "}";

    String SEPARATOR = "…";

    /**
     * Calls {@link #filter(String, Entry[])} converting the map in entries.
     *
     * @see #filter(String, Entry[])
     */
    static String filter (final String text, final Map<?, ?> parameters) {
        requireNotNull (parameters, "parameters");
        Set<? extends Entry<?, ?>> entries = parameters.entrySet ();
        return filter (text, entries.toArray (new Entry<?, ?>[0]));
    }

    /**
     * Filters a text substituting each key by its value. The keys format is:
     * <code>${key}</code> and all occurrences are replaced by the supplied value.
     *
     * <p>If a variable does not have a parameter, it is left as it is.
     *
     * @param text The text to filter. Can not be 'null'.
     * @param parameters The map with the list of key/value tuples. Can not be 'null'.
     * @return The filtered text or the same string if no values are passed or found in the text.
     */
    static String filter (final String text, final Entry<?, ?>... parameters) {
        requireNotNull (text, "text");
        requireNotNull (parameters, "parameters");

        String result = text;

        for (Entry<?, ?> parameter : parameters) {
            Object k = parameter.getKey ();
            Object v = parameter.getValue ();
            requireNotNull (k, "key");
            requireNotNull (v, "value");

            String key = stringOf (k);
            requireNotEmpty (key, "key");
            String value = stringOf (v);

            result = result.replace (VARIABLE_PREFIX + key + VARIABLE_SUFFIX, value);
        }

        return result;
    }

    /**
     * Utility method to check if a string has a value.
     *
     * @param text String to check.
     * @return True if the string is 'null' or empty.
     */
    static boolean isEmpty (String text) {
        return text == null || text.isEmpty ();
    }

    /**
     * Only a wrapper to avoid having to catch the checked exception.
     *
     * @see String#String(byte[], String)
     */
    static String encode (final byte[] data, final String encoding) {
        try {
            return new String (data, encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException (e);
        }
    }

    /**
     * Only a wrapper to avoid having to catch the checked exception.
     *
     * @see String#getBytes(String)
     */
    static byte[] decode (final String text, final String encoding) {
        try {
            return text.getBytes (encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException (e);
        }
    }

    /**
     * Repeat a given text a certain number of times.
     *
     * @param text String to repeat. It can't be 'null'.
     * @param times Number of times to repeat the text. Must be greater than 0.
     * @return The passed text repeated the given times.
     */
    static String repeat (String text, int times) {
        Checks.require (text != null);
        Checks.require (times >= 0);

        StringBuilder buffer = new StringBuilder (text.length () * times);

        for (int ii = 0; ii < times; ii++)
            buffer.append (text);

        return buffer.toString ();
    }

    /**
     * Indents every line with the given padding the number of times specified.
     *
     * @param text Text to indent. Can't be 'null'.
     * @param padding String to repeat at the beginning of each line. Can't be 'null'.
     * @param times Number of times to repeat the padding text. Must be greater than 0.
     * @return Text with every line indented with the given padding the number of times specified.
     */
    static String indent (final String text, final String padding, final int times) {
        Checks.require (text != null);

        String[] lines = text.split (System.lineSeparator (), -1);
        String appendString = repeat (padding, times);
        StringBuilder buffer = new StringBuilder ();

        for (int ii = 0; ii < lines.length - 1; ii++)
            buffer.append (appendString).append (lines[ii]).append (System.lineSeparator ());

        return buffer.append (appendString).append (lines[lines.length - 1]).toString ();
    }

    /**
     * Syntactic sugar for multiline strings.
     *
     * @param lines Array of lines. 'null' lines are ommited.
     * @return The multine strings composed of all lines.
     */
    static String lines (String... lines) {
        return lines == null?
            "" :
            Stream.of(lines)
                .filter (Objects::nonNull)
                .collect (joining (System.lineSeparator ()));
    }

    static String shortenEnd (String string, int chars) {
        return shorten (string, chars, SEPARATOR);
    }

    static String shortenMiddle (String string, int chars) {
        return (string.length () > (chars * 2) + 1)?
            string.substring (0, chars) + SEPARATOR + string.substring (string.length () - chars) :
            string;
    }

    static String shorten(String string, int max, String separator) {
        final int maxSize = Math.abs (max);
        final int length = string.length ();

        if (length > maxSize) {
            final int separatorLength = separator.length ();

            return max < 0?
                separator + string.substring (length - maxSize + separatorLength, length) :
                string.substring (0, maxSize - separatorLength) + separator;
        }
        else {
            return string;
        }
    }

    static boolean isBlank (final String text) {
        return (text == null? 0 : text.trim ().length ()) == 0;
    }

    static boolean isNotBlank (final String text) {
        return !isBlank (text);
    }

    static byte[] utf8Bytes(final String string) {
        return string == null || string.length () == 0?
            new byte[0] :
            string.getBytes (StandardCharsets.UTF_8);
    }

    static String hex (final byte [] data) {
        if (data == null || data.length == 0)
            return "";

        final StringBuilder result = new StringBuilder(data.length * 2);

        for (byte bb : data)
            result.append (String.format("%02x", bb));

        return result.toString ();
    }
}
