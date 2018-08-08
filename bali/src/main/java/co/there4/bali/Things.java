package co.there4.bali;

import static co.there4.bali.Checks.*;
import static co.there4.bali.Strings.*;
import static java.lang.System.getProperty;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.util.Objects;

/**
 * Should be 'Objects' but can cause clashes (ie: Groovy).
 *
 * @author jam
 */
public interface Things {
    boolean PRINT_HASH = getProperty ("Things.printHash") != null;

    /**
     * Defined as an alias because you can not import Objects.toString statically
     * (Object already defines toString). This way is less verbose.
     *
     * @see Objects#toString(Object)
     */
    static String stringOf (Object o) { return Objects.toString (o); }

    /**
     * Defined as an alias because you can not import Objects.equals statically
     * (Object already defines equals). This way is less verbose.
     *
     * @see Objects#equals(Object, Object)
     */
    static <T> boolean equal (T o1, T o2) { return Objects.equals (o1, o2); }

    /**
     * Prints an object for the purpose of implementing 'toString'.
     *
     * This is for printing 'this'
     *
     * @param o The object to print. It can't be 'null'.
     * @return The object's representation in a string.
     */
    static String printInstance (Object o, boolean printHash, String... fields) {
        checkArgument (o != null);
        checkArgument (fields != null);

        String className = o.getClass ().getSimpleName ();
        String fieldsString = stream (fields)
            .filter (field -> !field.isEmpty ())
            .collect (joining (", "));

        if (!fieldsString.isEmpty ())
            fieldsString = " {" + fieldsString + "}";

        return printHash?
            format ("%s@%s%s", className, o.hashCode (), fieldsString) :
            format ("%s%s", className, fieldsString);
    }

    static String printInstance (Object o, String... fields) {
        return printInstance (o, PRINT_HASH, fields);
    }

    static String printField (String name, Object value) {
        checkArgument (!isEmpty (name));

        return value == null || value.toString ().isEmpty ()? "" : format ("%s: %s", name, value);
    }
}
