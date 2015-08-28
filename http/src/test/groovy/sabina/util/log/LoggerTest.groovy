package sabina.util.log

import org.testng.annotations.Test

import static java.util.logging.Level.FINE
import static sabina.util.Console.*

import static sabina.util.Console.restoreOut
import static sabina.util.Configuration.parameters
import static sabina.util.Configuration.configuration
import static sabina.util.log.Logger.*

@Test class LoggerTest {
    @Test (expectedExceptions = IllegalArgumentException)
    public void "it is not allowed to get a logger with 'null' class" () {
        getLogger (null)
    }

    public void "getting a logger for a class returns a valid logger" () {
        Logger logger = getLogger (LoggerTest)
        assert logger instanceof Logger
    }

    public void "printing a log message writes the message in the handler" () {
        LogConfiguration.load ([
            ".level" : FINE.toString (),
            "handlers" : TerminalHandler.name,
            "sabina.util.log.TerminalHandler.formatter" : PatternFormat.name
        ])

        ByteArrayOutputStream baos = redirectOut ()

        Logger logger = getLogger (LoggerTest)

        logger.debug ("debug %d: %s", 1, "example")
        assert baos.toString ().contains ("debug 1: example")
        baos.reset ()

        logger.info ("info %d: %s", 1, "example")
        assert baos.toString ().contains ("info 1: example")
        baos.reset ()

        logger.warn ("warn %d: %s", 1, "example")
        assert baos.toString ().contains ("warn 1: example")
        baos.reset ()

        logger.error ("error %d: %s", 1, "example")
        assert baos.toString ().contains ("error 1: example")

        restoreOut ()
    }

    public void "printing a message with an exception lists the stack trace" () {
        ByteArrayOutputStream baos = redirectOut ()

        Logger logger = getLogger (LoggerTest)

        logger.error ("error %d: %s", new RuntimeException ("logged exception"), 1, "example")
        String loggedMessage = baos.toString ()
        assert loggedMessage.contains ("error 1: example")
        assert loggedMessage.contains ("logged exception")

        restoreOut ()
    }
}
