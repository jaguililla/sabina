package co.there4.bali;

import static co.there4.bali.Checks.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * @author jam
 */
public interface Exceptions {
    static Throwable filter (Throwable throwable, String prefix) {
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
