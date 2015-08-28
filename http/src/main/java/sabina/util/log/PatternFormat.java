/*
 * Copyright © 2011 Juan José Aguililla. All rights reserved.
 *
 * This program comes WITHOUT ANY WARRANTY; It is free software, you can redistribute it and/or
 * modify it under the terms of the GNU General Public License, Version 3.0.
 * You may obtain a copy of the License at: http://www.gnu.org/licenses/gpl.html
 */

package sabina.util.log;

import static java.lang.Thread.currentThread;
import static java.time.format.DateTimeFormatter.ofPattern;
import static sabina.util.Console.AnsiColor.*;
import static sabina.util.Console.AnsiEffect.BOLD;
import static sabina.util.Console.AnsiEffect.INVERSE;
import static sabina.util.Console.AnsiEffect.UNDERLINE;
import static sabina.util.Console.ansi;
import static sabina.util.Strings.EOL;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import sabina.util.Console;
import sabina.util.Exceptions;
import sabina.util.Strings;

/**
 * TODO .
 * TODO Set pattern in configuration
 * TODO Colored output for ERROR, WARN...
 *
 * @author jamming
 */
public final class PatternFormat extends Formatter {
    /** Chop adds ~ at the end of the truncated string */
    private boolean useColor, showDate, showPackage, chopThread;
    private String basePackage;

//    private final DateTimeFormatter dateTimeFormatter = ofPattern ("yyyy-MM-dd HH:mm:ss,SSS");
    private final DateTimeFormatter dateTimeFormatter = ofPattern ("HH:mm:ss,SSS");

    /*
     * TODO Setup: field lenghts, include package or not, include date or not
     */
    private final String pattern = "%s%s %-6s %-15s [%-10s]" + ansi() + " %s%n";

    /** {@inheritDoc} */
    @Override public String format (LogRecord record) {
        Instant instant = Instant.ofEpochMilli (record.getMillis ());
        LocalDateTime ldt = LocalDateTime.ofInstant (instant, ZoneOffset.UTC);

        Throwable thrown = record.getThrown ();
        String trace = thrown == null? "" : EOL + Exceptions.printThrowable (thrown);

        String level = record.getLevel ().toString ();
        String color = ansi (BLUE);

        switch (level) {
            case "INFO":
                color = ansi (GREEN, BOLD);
                break;
            case "WARNING":
                color = ansi (YELLOW, BOLD);
                break;
            case "ERROR":
                color = ansi (RED, BOLD);
                break;
        }

        return String.format (
            pattern,
            color,
            dateTimeFormatter.format (ldt),
            level,
            record.getLoggerName (),
            currentThread ().getName (),
            String.format (record.getMessage (), record.getParameters ()) + trace
        );
    }
}
