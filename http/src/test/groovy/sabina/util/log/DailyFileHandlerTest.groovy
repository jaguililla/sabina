package sabina.util.log

import org.testng.annotations.Test

import static sabina.util.log.LogConfiguration.load
import static java.util.logging.Level.*
import static sabina.util.log.Logger.getLogger

@Test class DailyFileHandlerTest {
    public void "a handler creates a new file if it does not exists" () {
        load ([
            ".level" : FINE.toString (),
            "handlers" : DailyFileHandler.name,
            (DailyFileHandler.name + ".formatter") : PatternFormat.name,
            (DailyFileHandler.name + ".formatter.useColor") : false.toString (),
            (DailyFileHandler.name + ".level") : FINE.toString ()
        ])

        Logger logger = getLogger (LoggerTest)
        logger.info ("log file")
    }

    public void "a handler appends to today's log file if it exists" () {

    }

    public void "a handler creates a new log file if the day changes" () {

    }

    public void "a handler deletes old log files if they exceed maximum number" () {

    }
}
