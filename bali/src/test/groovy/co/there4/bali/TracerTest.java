package co.there4.bali;

import static ch.qos.logback.classic.Level.*;
import static ch.qos.logback.classic.spi.ThrowableProxyUtil.asString;
import static org.slf4j.LoggerFactory.getILoggerFactory;
import static org.slf4j.LoggerFactory.getLogger;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import org.testng.annotations.Test;

/**
 * TODO Test 'null' arguments.
 *
 * @author jam
 */
@Test public class TracerTest implements Tracer {
    private String lastTrace;

    @Override public org.slf4j.Logger logger () {
        Logger logger = (Logger)getLogger (TracerTest.class);
        logger.setLevel (ALL);

        AppenderBase<ILoggingEvent> stringAppender = new AppenderBase<ILoggingEvent> () {
            @Override protected void append (ILoggingEvent eventObject) {
                String message = eventObject.getMessage ();
                IThrowableProxy throwable = eventObject.getThrowableProxy ();
                String messageText = message == null? "" : message;
                String throwableText = throwable == null? "" : "\n" + asString (throwable);
                lastTrace = messageText + throwableText;
            }
        };

        stringAppender.setContext((LoggerContext)getILoggerFactory());
        stringAppender.start();

        logger.addAppender (stringAppender);
        return logger;
    }

    private String checkLog (Runnable r) {
        r.run ();
        return lastTrace;
    }

    public void logger_writes_formatted_messages () {
        assert checkLog (() -> trace ("msg %s %s", "a1", "a2")).contains ("msg a1 a2");
        assert checkLog (() -> debug ("msg %s %s", "a1", "a2")).contains ("msg a1 a2");
        assert checkLog (() -> info ("msg %s %s", "a1", "a2")).contains ("msg a1 a2");
        assert checkLog (() -> warn ("msg %s %s", "a1", "a2")).contains ("msg a1 a2");
        assert checkLog (() -> error ("msg %s %s", "a1", "a2")).contains ("msg a1 a2");

        final RuntimeException e = new RuntimeException ();

        String out = checkLog (() -> warn ("msg %s %s", e, "a1", "a2"));
        assert out.contains ("msg a1 a2") && out.contains (RuntimeException.class.getName ());

        out = checkLog (() -> error ("msg %s %s", e, "a1", "a2"));
        assert out.contains ("msg a1 a2") && out.contains (RuntimeException.class.getName ());
    }

    @Test (enabled = false, description = "TODO Fix this test")
    public void logger_does_not_write_messages_if_level_is_disabled () {
        Logger logger = (Logger)logger ();

        logger.setLevel (DEBUG);
        assert checkLog (() -> trace ("msg %s %s", "a1", "a2")).equals ("");
        logger.setLevel (INFO);
        assert checkLog (() -> debug ("msg %s %s", "a1", "a2")).equals ("");
        logger.setLevel (WARN);
        assert checkLog (() -> info ("msg %s %s", "a1", "a2")).equals ("");
        logger.setLevel (ERROR);
        assert checkLog (() -> warn ("msg %s %s", "a1", "a2")).equals ("");
        logger.setLevel (OFF);
        assert checkLog (() -> error ("msg %s %s", "a1", "a2")).equals ("");
    }
}
