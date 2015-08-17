package sabina.util.log

import org.testng.annotations.Test
import sabina.util.Settings

import java.util.logging.Handler
import java.util.logging.LogManager

import static java.util.logging.Level.*
import static sabina.util.Settings.parameters
import static sabina.util.Settings.settings
import static sabina.util.log.Logger.*

@Test class LoggerTest {
    @Test (expectedExceptions = IllegalArgumentException)
    public void "setting up the log module with a 'null' resource fails" () {
        setup (null)
    }

    public void "setting up the log module with a not existent resource do nothing" () {
        setup ("")
        setup ("/not-found.properties")
    }

    @Test (expectedExceptions = IllegalArgumentException)
    public void "it is not allowed to get a logger with 'null' class" () {
        getLogger (null)
    }

    public void "getting a logger for a class returns a valid logger" () {
        Logger logger = getLogger (LoggerTest)
        assert logger instanceof Logger
    }

    public void "printing a log message writes the message in the handler" () {
        PrintStream o = System.out
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        System.out = new PrintStream(baos)

        settings ().load (
            parameters (["--logging.level", "fine"] as String [])
        )

        setup ("sabina.properties")
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
        baos.reset ()

        System.out = o
    }
}
