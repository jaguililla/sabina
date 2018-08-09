
package co.there4.bali;

import static java.lang.String.format;

/**
 * Utilities to check variable states and parameters.
 */
public interface Checks {
    static void checkArgument (boolean condition) {
        if (!condition)
            throw new IllegalArgumentException ();
    }

    static void checkArgument (boolean condition, String message, Object... arguments) {
        if (!condition)
            throw new IllegalArgumentException (format (message, arguments));
    }
}
