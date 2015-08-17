/*
 * Copyright © 2011 Juan José Aguililla. All rights reserved.
 *
 * This program comes WITHOUT ANY WARRANTY; It is free software, you can redistribute it and/or
 * modify it under the terms of the GNU General Public License, Version 3.0.
 * You may obtain a copy of the License at: http://www.gnu.org/licenses/gpl.html
 */

package sabina.util.log;

import static java.lang.ClassLoader.getSystemClassLoader;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.logging.Level.*;
import static java.util.logging.LogManager.getLogManager;
import static sabina.util.Checks.checkArgument;
import static sabina.util.Exceptions.printThrowable;
import static sabina.util.Strings.EOL;
import static sabina.util.Strings.isEmpty;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;

import sabina.util.Exceptions;
import sabina.util.Strings;

/**
 * TODO .
 *
 * @author jamming
 */
public final class Logger {
    static {
        // Set configuration class name

    }

    public static void setup (String resource) {
        checkArgument (resource != null);

        try {
            getLogManager ().readConfiguration (
                getSystemClassLoader ().getResourceAsStream (resource)
            );
        }
        catch (Exception e) {
            e.printStackTrace(); // Do not use a logger here as we are setting up them
        }
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

    public void debug (String message, Object... parameters) {
        log (FINE, message, parameters);
    }

    public void info (String message, Object... parameters) {
        log (INFO, message, parameters);
    }

    public void warn (String message, Object... parameters) {
        log (WARNING, message, parameters);
    }

    public void error (String message, Object... parameters) {
        log (SEVERE, message, parameters);
    }

    public void error (String message, Throwable exception, Object... parameters) {
        log (SEVERE, message + EOL + printThrowable (exception), parameters);
    }

    private void log (Level level, String message, Object... parameters) {
        if (logger.isLoggable (level))
            logger.log (level, format (message, parameters));
    }
}
