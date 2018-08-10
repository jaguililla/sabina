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
        require (throwable != null);
        require (prefix != null);

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
        require (throwable != null);

        @SuppressWarnings ("ThrowableResultOfMethodCallIgnored")
        Throwable e = prefix.isEmpty ()? throwable : filter (throwable, prefix);
        StringWriter out = new StringWriter ();
        e.printStackTrace (new PrintWriter (out));
        return out.toString ();
    }

    static RuntimeException propagate (Throwable throwable) {
        if (throwable instanceof RuntimeException)
            throw (RuntimeException)throwable;
        else
            throw new RuntimeException(throwable); // NOSONAR A generic runtime exception is OK here
    }

    static Throwable getCause (final Throwable throwable) {
        requireNotNull (throwable, "exception");

        final Throwable cause = throwable.getCause ();

        if (cause == null)
            return throwable;
        else
            return getCause (throwable.getCause ());
    }

    static void requireException (
        final Class<? extends Exception> exceptionType, final UncheckedRunnable block) {

        requireNotNull (exceptionType, "exception type");
        requireNotNull (block, "block");

        try {
            block.run ();
            throw new AssertionError ();
        }
        catch (Exception e) {
            final boolean isExpectedException = exceptionType.isAssignableFrom (e.getClass ());

            if (!isExpectedException) {
                final String message = String.format (
                    "Thrown exception: %s is not the expected one: %s",
                    e.getClass ().getName (),
                    exceptionType.getName ()
                );

                throw new IllegalStateException (message);
            }
        }
    }
}
