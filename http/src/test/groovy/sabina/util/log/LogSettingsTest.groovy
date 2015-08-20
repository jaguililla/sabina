package sabina.util.log

import org.testng.annotations.Test

import static java.util.logging.Level.INFO
import static sabina.util.Configuration.parameters
import static sabina.util.Configuration.configuration
import static sabina.util.log.Logger.getLogger
import static sabina.util.Console.*

@Test public class LogSettingsTest {
    public void "change logging setting updates the log configuration" () {
        configuration ().load (
            parameters ([
                "--logging.level", INFO.toString (),
                "--logging.handlers", TerminalHandler.name,
                "--logging.sabina.util.log.TerminalHandler.formatter", PatternFormat.name
            ] as String [])
        )
        LogSettings.load ()
        ByteArrayOutputStream baos = redirectOut ()

        Logger logger = getLogger (LoggerTest)

        logger.fine ("debug %d: %s", 1, "example")
        assert baos.toString ().isEmpty ()
        baos.reset ()

        logger.info ("info %d: %s", 1, "example")
        assert baos.toString ().contains ("info 1: example")
        baos.reset ()

        logger.warning ("warn %d: %s", 1, "example")
        assert baos.toString ().contains ("warn 1: example")
        baos.reset ()

        logger.severe ("error %d: %s", 1, "example")
        assert baos.toString ().contains ("error 1: example")

        restoreOut ()
    }
}
