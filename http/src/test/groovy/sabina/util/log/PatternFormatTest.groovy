package sabina.util.log

import org.testng.annotations.Test

import java.util.logging.LogRecord

import static java.util.logging.Level.FINE
import static sabina.util.Console.AnsiColor.*
import static sabina.util.Console.ansi

@Test class PatternFormatTest {
    private static boolean contains (String string, String... strings) {
        strings.inject (true, { a, b -> a && string.contains (b) })
    }

    public void "the default format renders messages properly" () {
        PatternFormat format = new PatternFormat()

        LogRecord record = new LogRecord(FINE, "msg")
        record.loggerName = "bar"
        record.millis = 0

        String msg = format.format (record)
        println msg

        assert contains (msg,
            "00:00:00,000", "FINE", "bar", "msg", ansi (CYAN), ansi (MAGENTA), ansi (BLUE), ansi ()
        )
    }

    public void "changing the format renders messages properly" () {
        PatternFormat format = new PatternFormat()
        format.useColor = false
        format.pattern = "%tF %s [%s]"

        LogRecord record = new LogRecord(FINE, "msg")
        record.loggerName = "bar"
        record.millis = 0

        String msg = format.format (record)
        println msg

        assert msg == "1970-01-01 FINE [bar]"
    }
}
