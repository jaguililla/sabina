/*
 * Copyright © 2011 Juan José Aguililla. All rights reserved.
 *
 * This program comes WITHOUT ANY WARRANTY; It is free software, you can redistribute it and/or
 * modify it under the terms of the GNU General Public License, Version 3.0.
 * You may obtain a copy of the License at: http://www.gnu.org/licenses/gpl.html
 */

package sabina.util.log;

import static java.lang.String.format;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;

import java.util.logging.LogManager;

/**
 * TODO .
 *
 * @author jamming
 */
public class Logger extends java.util.logging.Logger {
    /** TODO . */
    private static final String LOGGING_SETTINGS_RESOURCE = "/log.properties";

    /**
     * The logging manager property is set here, because this class is loaded before 'LogManager'
     */
    static {
        try {
            LogManager.getLogManager ().readConfiguration (
                Class.class.getResourceAsStream (LOGGING_SETTINGS_RESOURCE));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Logger getLogger (Class<?> clazz) {
        return getLogger (clazz, null);
    }

    public static Logger getLogger (Class<?> clazz, String bundleName) {
        String name = clazz.getName ();
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(name, bundleName);

        return new Logger (logger.getName (), logger.getResourceBundleName ());
    }

    public Logger (String aName, String bundleName) {
        super (aName, bundleName);
    }

    public void caller () {
        if (isLoggable (FINE)) {
            StackTraceElement stack = Thread.currentThread ().getStackTrace () [3];
            log (FINE,
                stack.getClassName () + '.' + stack.getMethodName ()
                + " (" + stack.getLineNumber () + ')');
        }
    }

    public void debug (String message, Object... parameters) {
        log (FINE, format (message, parameters));
    }

    public void error (String message, Throwable exception) {
        log (SEVERE, message);
    }

    public void info (String message, Object... parameters) {
        log (INFO, format (message, parameters));
    }
}
