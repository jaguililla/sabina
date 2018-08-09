package co.there4.bali;

import static co.there4.bali.Checks.*;
import static java.util.Arrays.stream;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author jam
 */
public interface Throwables {
    static Throwable filter (Throwable throwable, String prefix) {
        checkArgument (throwable != null);
        checkArgument (prefix != null);

        if (!prefix.isEmpty ()) {
            final StackTraceElement[] stackTrace = throwable.getStackTrace ();

            throwable.setStackTrace (
                stream (stackTrace)
                    .filter (frame -> frame.getClassName ().startsWith (prefix))
                    .toArray (StackTraceElement[]::new)
            );
        }

        return throwable;
    }

    static String printThrowable (Throwable t) {
        return printThrowable (t, "");
    }

    static String printThrowable (Throwable throwable, String prefix) {
        checkArgument (throwable != null);

        @SuppressWarnings ("ThrowableResultOfMethodCallIgnored")
        Throwable e = prefix.isEmpty ()? throwable : filter (throwable, prefix);
        StringWriter out = new StringWriter ();
        e.printStackTrace (new PrintWriter (out));
        return out.toString ();
    }
}
