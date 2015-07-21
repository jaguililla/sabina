package sabina.util;

import static java.lang.String.format;

public final class Objects {
    public static <T> boolean equal (T a, T b) { return java.util.Objects.equals (a, b); }

    public static String stringOf (Object a) { return java.util.Objects.toString (a); }

    public static String print (Object o) {
        return format ("%s [%s]\n", o.getClass ().getSimpleName (), o.hashCode ());
    }

    public static int hash (Object... values) {
        return java.util.Objects.hash (values);
    }

    public static String print (String name, Object value) {
        return value == null? "" : format ("%s : %s\n", name, value);
    }

    private Objects () {
        throw new IllegalStateException ();
    }
}
