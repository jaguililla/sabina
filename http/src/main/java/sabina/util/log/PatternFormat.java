/*
 * Copyright © 2011 Juan José Aguililla. All rights reserved.
 *
 * This program comes WITHOUT ANY WARRANTY; It is free software, you can redistribute it and/or
 * modify it under the terms of the GNU General Public License, Version 3.0.
 * You may obtain a copy of the License at: http://www.gnu.org/licenses/gpl.html
 */

package sabina.util.log;

import static java.lang.Thread.currentThread;
import static java.time.ZoneOffset.UTC;
import static java.util.logging.Level.*;
import static sabina.util.Console.AnsiColor.*;
import static sabina.util.Console.ansi;
import static sabina.util.Exceptions.printThrowable;
import static sabina.util.Strings.EOL;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import sabina.util.Console.AnsiColor;

/**
 * TODO Set pattern in configuration
 * TODO Colored output for ERROR, WARN...
 *
 * @author jamming
 */
public final class PatternFormat extends Formatter {
    private AnsiColor threadColor = CYAN;
    private AnsiColor loggerColor = MAGENTA;

    private boolean useColor = true;
    private String pattern = "%tH:%<tM:%<tS,%<tL %s%-6s%s %s%-20s%s [%s%-10s%s] %s%n";

    private Map<Level, AnsiColor> levelColors = new HashMap<> ();

    public PatternFormat () {
        levelColors.put (FINE, BLUE);
        levelColors.put (INFO, GREEN);
        levelColors.put (WARNING, YELLOW);
        levelColors.put (SEVERE, RED);
    }

    /** {@inheritDoc} */
    @Override public String format (LogRecord record) {
        Instant instant = Instant.ofEpochMilli (record.getMillis ());
        LocalDateTime dateTime = LocalDateTime.ofInstant (instant, UTC);

        Throwable thrown = record.getThrown ();
        String trace = thrown == null?
            "" : EOL + ansi (RED) + printThrowable (thrown) + ansi ();

        Level level = record.getLevel ();
        String levelColor = levelColors.containsKey (level)?
            ansi (levelColors.get (level)) : "";

        return useColor ?
            String.format (
                pattern,
                dateTime,
                levelColor,
                level,
                ansi (),
                ansi (loggerColor),
                record.getLoggerName (),
                ansi (),
                ansi (threadColor),
                currentThread ().getName (),
                ansi (),
                String.format (record.getMessage (), record.getParameters ()) + trace
            ) :
            String.format (
                pattern,
                dateTime,
                level,
                record.getLoggerName (),
                currentThread ().getName (),
                String.format (record.getMessage (), record.getParameters ()) + trace
            );
    }

    public AnsiColor getFineColor () { return levelColors.get (FINE); }
    public void setFineColor (AnsiColor color) { levelColors.put (FINE, color); }
    public AnsiColor getInfoColor () { return levelColors.get (INFO); }
    public void setInfoColor (AnsiColor color) { levelColors.put (INFO, color); }
    public AnsiColor getWarningColor () { return levelColors.get (WARNING); }
    public void setWarningColor (AnsiColor color) { levelColors.put (WARNING, color); }
    public AnsiColor getSevereColor () { return levelColors.get (SEVERE); }
    public void setSevereColor (AnsiColor color) { levelColors.put (SEVERE, color); }

    public AnsiColor getLoggerColor () { return loggerColor; }
    public void setLoggerColor (AnsiColor loggerColor) { this.loggerColor = loggerColor; }
    public String getPattern () { return pattern; }
    public void setPattern (String pattern) { this.pattern = pattern; }
    public AnsiColor getThreadColor () { return threadColor; }
    public void setThreadColor (AnsiColor threadColor) { this.threadColor = threadColor; }
    public boolean isUseColor () { return useColor; }
    public void setUseColor (boolean useColor) { this.useColor = useColor; }
}
