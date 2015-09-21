/*
 * Copyright © 2011 Juan José Aguililla. All rights reserved.
 *
 * This program comes WITHOUT ANY WARRANTY; It is free software, you can redistribute it and/or
 * modify it under the terms of the GNU General Public License, Version 3.0.
 * You may obtain a copy of the License at: http://www.gnu.org/licenses/gpl.html
 */

package sabina.util.log;

import static java.lang.Thread.currentThread;
import static java.time.ZoneId.systemDefault;
import static java.util.logging.Level.*;
import static sabina.util.Builders.*;
import static sabina.util.Console.AnsiColor.*;
import static sabina.util.Console.ansi;
import static sabina.util.Exceptions.printThrowable;
import static sabina.util.Strings.EOL;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.logging.*;

import sabina.util.Console.AnsiColor;

/**
 * TODO Set pattern in configuration
 * TODO Colored output for ERROR, WARN...
 *
 * @author jamming
 */
public final class PatternFormat extends Formatter {
    private static final String COLOR_PATTERN =
        "%tH:%<tM:%<tS,%<tL %s%-6s%s %s%-20s%s [%s%-10s%s] %s%n";
    private static final String PATTERN = "%tH:%<tM:%<tS,%<tL %-6s %-20s [%-10s] %s%n";

    private AnsiColor threadColor = CYAN;
    private AnsiColor loggerColor = MAGENTA;

    private boolean useColor = true;
    private String pattern = COLOR_PATTERN;

    private Map<Level, AnsiColor> levelColors = tmap (
        entry (FINE, BLUE),
        entry (INFO, GREEN),
        entry (WARNING, YELLOW),
        entry (SEVERE, RED)
    );

    public PatternFormat () {
        LogManager manager = LogManager.getLogManager();
        String cname = getClass().getName();
//        setUseColor (manager.getLevelProperty(cname +".level", Level.INFO));
    }

    /** {@inheritDoc} */
    @Override public String format (LogRecord record) {
        Instant instant = Instant.ofEpochMilli (record.getMillis ());
        LocalDateTime dateTime = LocalDateTime.ofInstant (instant, systemDefault ());

        Throwable thrown = record.getThrown ();
        String trace = thrown == null?
            "" :
            useColor?
                ansi (RED) + EOL + printThrowable (thrown) + ansi () :
                EOL + printThrowable (thrown);

        Level level = record.getLevel ();
        String levelColor = levelColors.containsKey (level)?
            ansi (levelColors.get (level)) : ansi (BLUE);

        final String message = record.getMessage ();
        final Object[] parameters = record.getParameters ();

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
                String.format (message, parameters) + trace
            ) :
            String.format (
                pattern,
                dateTime,
                level,
                record.getLoggerName (),
                currentThread ().getName (),
                String.format (message, parameters) + trace
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
    public void setUseColor (boolean useColor) {
        this.useColor = useColor;
        this.pattern = useColor? COLOR_PATTERN : PATTERN;
    }
}
