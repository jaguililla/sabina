package sabina.util;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static sabina.util.Checks.checkArgument;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * @author jam
 */
public final class Exceptions {
    public static Throwable filter (Throwable throwable, String prefix) {
        checkArgument (throwable != null);
        checkArgument (prefix != null);

        if (!prefix.isEmpty ()) {
            List<StackTraceElement> stack = stream (throwable.getStackTrace ())
                .filter (frame -> frame.getClassName ().startsWith (prefix))
                .collect (toList());

            throwable.setStackTrace (stack.toArray (new StackTraceElement[stack.size()]));
        }

        return throwable;
    }

    public static String printThrowable (Throwable t) {
        return printThrowable (t, "");
    }

    public static String printThrowable (Throwable throwable, String prefix) {
        checkArgument (throwable != null);

        Throwable e = prefix.isEmpty ()? throwable : filter (throwable, prefix);
        StringWriter out = new StringWriter ();
        e.printStackTrace (new PrintWriter (out));
        return out.toString ();
    }

    static void _create () { new Exceptions (); }

    private Exceptions () {
        throw new IllegalStateException ();
    }
}
