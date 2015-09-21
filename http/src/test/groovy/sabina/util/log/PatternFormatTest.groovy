package sabina.util.log

import org.testng.annotations.Test

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
            "01:00:00,000", "FINE", "bar", "msg", ansi (CYAN), ansi (MAGENTA), ansi (BLUE), ansi ()
        )
    }

    public void "changing the format renders messages properly" () {
        PatternFormat format = new PatternFormat()
        format.useColor = false
        format.pattern = "%tF %s [%s]"

        assert format.pattern == "%tF %s [%s]"
        assert !format.useColor

        LogRecord record = new LogRecord(FINE, "msg")
        record.loggerName = "bar"
        record.millis = 0

        String msg = format.format (record)
        println msg

        assert msg == "1970-01-01 FINE [bar]"
    }

    public void "formatting a log with a stack trace prints it in another line" () {
        PatternFormat format = new PatternFormat()

        LogRecord record = new LogRecord(FINE, "msg")
        record.loggerName = "bar"
        record.thrown = new RuntimeException("logged exception")
        record.millis = 0

        String msg = format.format (record)
        println msg

        assert contains (msg,
            "01:00:00,000", "FINE", "bar", "msg", ansi (CYAN), ansi (MAGENTA), ansi (BLUE), ansi (),
            "RuntimeException", "logged exception", ansi (RED)
        )

        format.useColor = false

        msg = format.format (record)
        println msg

        assert contains (msg,
            "01:00:00,000", "FINE", "bar", "msg", "RuntimeException", "logged exception"
        )
    }

    public void "changing colors sets proper values" () {
        def format = new PatternFormat ()

        format.setFineColor (WHITE)
        assert format.getFineColor () == WHITE

        format.setInfoColor (WHITE)
        assert format.getInfoColor () == WHITE

        format.setWarningColor (WHITE)
        assert format.getWarningColor () == WHITE

        format.setSevereColor (WHITE)
        assert format.getSevereColor () == WHITE

        format.setLoggerColor (WHITE)
        assert format.getLoggerColor () == WHITE

        format.setThreadColor (WHITE)
        assert format.getThreadColor () == WHITE
    }
}
