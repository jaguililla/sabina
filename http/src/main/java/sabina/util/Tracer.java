package sabina.util;

import static java.lang.System.getProperty;
import static org.slf4j.LoggerFactory.getLogger;
import static java.lang.String.format;

import org.slf4j.Logger;

public interface Tracer {
    String FLARE_PREFIX = getProperty ("sabina.util.Tracer.flarePrefix", ">>>> ");
    String ENTER_PREFIX = getProperty ("sabina.util.Tracer.enterPrefix", "-> ");
    String EXIT_PREFIX = getProperty ("sabina.util.Tracer.enterPrefix", "<- ");

    default Logger logger () { return getLogger (getClass ()); }

    default void debug (String format, Object... arguments) {
        final Logger logger = logger ();
        if (logger.isDebugEnabled ())
            logger.debug (format (format, arguments));
    }

    default void error (String format, Object... arguments) {
        final Logger logger = logger ();
        if (logger.isErrorEnabled ())
            logger.error (format (format, arguments));
    }

    default void error (String msg, Throwable t, Object... arguments) {
        final Logger logger = logger ();
        if (logger.isErrorEnabled ())
            logger.error (format (msg, arguments), t);
    }

    default void info (String format, Object... arguments) {
        final Logger logger = logger ();
        if (logger.isInfoEnabled ())
            logger.info (format (format, arguments));
    }

    default void trace (String format, Object... arguments) {
        final Logger logger = logger ();
        if (logger.isTraceEnabled ())
            logger.trace (format (format, arguments));
    }

    default void warn (String format, Object... arguments) {
        final Logger logger = logger ();
        if (logger.isWarnEnabled ())
            logger.warn (format (format, arguments));
    }

    default void warn (String msg, Throwable t, Object... arguments) {
        final Logger logger = logger ();
        if (logger.isWarnEnabled ())
            logger.warn (format (msg, arguments), t);
    }

    default void exit () {
        trace (ENTER_PREFIX);
    }

    default void enter () {
        trace (EXIT_PREFIX);
    }

    default void banner (String msg) {
        Strings.repeat (FLARE_PREFIX, msg.length ()); // longest line length
    }

    default void flare (String msg) {
        trace (FLARE_PREFIX + msg);
    }

    default void timeMillis (String msg, long startMillis) {
        trace(msg + format ("%d ms", (System.currentTimeMillis() - startMillis)));
    }

    default void timeNanos (String msg, long startNanos) {
        trace(msg + format ("%d ms", (System.nanoTime () - startNanos)));
    }
}
