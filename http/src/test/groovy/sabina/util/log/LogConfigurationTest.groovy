package sabina.util.log

import org.testng.annotations.Test

import static java.util.logging.Level.INFO
import static sabina.util.log.Logger.getLogger
import static sabina.util.Console.*

@Test class LogConfigurationTest {
    public void "change logging setting updates the log configuration" () {
        LogConfiguration.load ([
            ".level" : INFO.toString (),
            "handlers" : TerminalHandler.name,
            "sabina.util.log.TerminalHandler.formatter" : PatternFormat.name
        ])

        ByteArrayOutputStream baos = redirectOut ()

        Logger logger = getLogger (LoggerTest)

        logger.debug ("debug %d: %s", 1, "example")
        assert baos.toString ().isEmpty ()
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
}
