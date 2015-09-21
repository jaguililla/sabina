/*
 * Copyright © 2011 Juan José Aguililla. All rights reserved.
 *
 * This program comes WITHOUT ANY WARRANTY; It is free software, you can redistribute it and/or
 * modify it under the terms of the GNU General Public License, Version 3.0.
 * You may obtain a copy of the License at: http://www.gnu.org/licenses/gpl.html
 */

package sabina.util.log;

import static java.lang.System.setProperty;
import static java.util.logging.Level.*;
import static sabina.util.Checks.checkArgument;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * TODO .
 *
 * @author jamming
 */
public final class Logger {
    private static final String LOGGING_CONFIG_PROPERTY = "java.util.logging.config.class";

    static {
        setProperty (LOGGING_CONFIG_PROPERTY, LogConfiguration.class.getName ());
    }

    public static Logger getLogger (Class<?> clazz) {
        return getLogger (clazz, null);
    }

    public static Logger getLogger (Class<?> clazz, String bundleName) {
        checkArgument (clazz != null);

        String name = clazz.getName ();
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(name, bundleName);

        return new Logger (logger);
    }

    private final java.util.logging.Logger logger;

    private Logger (java.util.logging.Logger logger) {
        this.logger = logger;
    }

    public void fine (String message, Object... parameters) {
        log (FINE, message, null, parameters);
    }

    public void info (String message, Object... parameters) {
        log (INFO, message, null, parameters);
    }

    public void warning (String message, Object... parameters) {
        log (WARNING, message, null, parameters);
    }

    public void severe (String message, Object... parameters) {
        log (SEVERE, message, null, parameters);
    }

    public void severe (String message, Throwable exception, Object... parameters) {
        log (SEVERE, message, exception, parameters);
    }

    public void debug (String message, Object... parameters) {
        fine (message, parameters);
    }

    public void warn (String message, Object... parameters) {
        warning (message, parameters);
    }

    public void error (String message, Object... parameters) {
        severe (message, parameters);
    }

    public void error (String message, Throwable exception, Object... parameters) {
        severe (message, exception, parameters);
    }

    /**
     * TODO .
     *
     * @param level
     * @param message
     * @param parameters
     */
    private void log (Level level, String message, Throwable thrown, Object... parameters) {
        if (logger.isLoggable (level)) {
            LogRecord record = new LogRecord (level, message);
            record.setLoggerName (logger.getName ());
            record.setThrown (thrown);
            record.setParameters (parameters);
            logger.log (record);
        }
    }
}
