package sabina.util;

import static com.sun.javafx.runtime.SystemProperties.getProperty;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static sabina.util.Checks.checkArgument;
import static sabina.util.Strings.isEmpty;

import java.util.Objects;

/**
 * Should be 'Objects' but can cause clashes (ie: Groovy).
 *
 * @author jam
 */
public final class Things {
    private static boolean printHash = getProperty ("sabina.util.Things.printHash") != null;

    public static void printHash (boolean print) {
        printHash = print;
    }

    /**
     * Defined as an alias because you can not import Objects.toString statically
     * (Object already defines toString). This way is less verbose.
     *
     * @see Objects#toString(Object)
     */
    public static String stringOf (Object o) { return Objects.toString (o); }

    /**
     * Defined as an alias because you can not import Objects.equals statically
     * (Object already defines equals). This way is less verbose.
     *
     * @see Objects#equals(Object, Object)
     */
    public static <T> boolean equal (T o1, T o2) { return Objects.equals (o1, o2); }

    /**
     * Prints an object for the purpose of implementing 'toString'.
     *
     * This is for printing 'this'
     *
     * @param o
     * @return
     */
    public static String printInstance (Object o, String... fields) {
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

    public static String printField (String name, Object value) {
        checkArgument (!isEmpty (name));

        return value == null || value.toString ().isEmpty ()? "" : format ("%s: %s", name, value);
    }

    static void _create () { new Things (); }

    private Things () {
        throw new IllegalStateException ();
    }
}
