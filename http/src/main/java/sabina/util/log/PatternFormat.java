/*
 * Copyright © 2011 Juan José Aguililla. All rights reserved.
 *
 * This program comes WITHOUT ANY WARRANTY; It is free software, you can redistribute it and/or
 * modify it under the terms of the GNU General Public License, Version 3.0.
 * You may obtain a copy of the License at: http://www.gnu.org/licenses/gpl.html
 */

package sabina.util.log;

import static sabina.util.Strings.EOL;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

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
    /*
     * TODO Setup: field lenghts, include package or not, include date or not
     * TODO Three files: log, errors y libs
     */
    private final String pattern = "%s  %-6s %-20s %-10s: %s%n";

    /** {@inheritDoc} */
    @Override public String format (LogRecord record) {
        Instant instant = Instant.ofEpochMilli(record.getMillis ());
        LocalDateTime ldt = LocalDateTime.ofInstant (instant, ZoneOffset.UTC);

        Throwable thrown = record.getThrown ();
        String trace = thrown == null? "" : EOL + Exceptions.printThrowable (thrown);

        return String.format (
            pattern,
            DateTimeFormatter.ofPattern ("yyyy-MM-dd HH:mm:ss,SSS").format (ldt),
            record.getLevel (),
            record.getLoggerName (),
            Thread.currentThread ().getName (),
            String.format (record.getMessage (), record.getParameters ()) + trace
        );
    }
}
