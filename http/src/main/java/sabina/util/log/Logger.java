/*
 * Copyright © 2011 Juan José Aguililla. All rights reserved.
 *
 * This program comes WITHOUT ANY WARRANTY; It is free software, you can redistribute it and/or
 * modify it under the terms of the GNU General Public License, Version 3.0.
 * You may obtain a copy of the License at: http://www.gnu.org/licenses/gpl.html
 */

package sabina.util.log;

import static java.lang.String.format;
import static java.lang.System.setProperty;
import static java.util.logging.Level.*;
import static java.util.logging.LogManager.getLogManager;
import static sabina.util.Checks.checkArgument;
import static sabina.util.Exceptions.printThrowable;
import static sabina.util.Strings.EOL;

import java.util.logging.Handler;
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
        setProperty (LOGGING_CONFIG_PROPERTY, LogSettings.class.getName ());
        LogSettings.load ();
    }

    public static Logger getLogger (Class<?> clazz) {
        return getLogger (clazz, null);
    }

    public static Logger getLogger (Class<?> clazz, String bundleName) {
        checkArgument (clazz != null);

        String name = clazz.getName ();
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(name, bundleName);

        logger.addHandler (new TerminalHandler());

        for (Handler h : logger.getHandlers ())
            h.setLevel (FINEST);

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

    /**
     * TODO Use THROWN and PARAMETERS in Logger.log (check this things before!)
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
