
package co.there4.bali;

/**
 * Utilities to check variable states and parameters.
 */
public interface Checks {
    static void require (boolean condition, String message) {
        if (!condition)
            throw new IllegalArgumentException (message);
    }

    static void require (boolean condition) {
        require (condition, null);
    }

    static void requireNotNull (Object parameter, String name) {
        require (parameter != null, "'" + name + "' can't be 'null'");
    }

    static void requireNotEmpty (String parameter, String name) {
        requireNotNull (parameter, name);
        require (!parameter.isEmpty (), "'" + name + "' can't be empty");
    }

    static void check (boolean condition, String message) {
        if (!condition)
            throw new IllegalStateException (message);
    }

    static void check (boolean condition) {
        check (condition, null);
    }

    static void checkNotNull (Object variable, String name) {
        check (variable != null, "'" + name + "' can't be 'null'");
    }

    static void checkNotEmpty (String variable, String name) {
        checkNotNull (variable, name);
        check (!variable.isEmpty (), "'" + name + "' can't be empty");
    }

    static void checkEmpty (String variable, String name) {
        check (variable == null || variable.isEmpty (), "'" + name + "' must be empty");
    }
}
