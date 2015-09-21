package sabina.util.log;

import static java.util.logging.Level.*;
import static java.util.logging.LogManager.getLogManager;
import static sabina.util.Builders.entry;
import static sabina.util.Builders.map;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * Load log settings from Configuration. If no settings with 'logging' prefix are found a default
 * configuration is loaded.
 *
 * Any errors in the logging config throws a runtime exception. It may stop the app, that's
 * probably what you want to do if you start with bad settings.
 *
 */
public final class LogConfiguration {
    /**
     * To avoid calling 'new LogConfiguration ()' in code. Who the hell designed Java logging?
     */
    public static void load (Map<?, ?> configuration) {
        new LogConfiguration (configuration);
    }

    public static void load () {
        new LogConfiguration ();
    }

    public LogConfiguration () {
        this (map ());
    }

    public LogConfiguration (Map<?, ?> configuration) {
        try {
            InputStream is = mapToStream (configuration.isEmpty ()?
                loadDefaults () : configuration);

            getLogManager ().readConfiguration (is);
        }
        catch (IOException e) {
            throw new RuntimeException (e);
        }
    }

    InputStream mapToStream (Map<?, ?> map) throws IOException {
        Properties p = new Properties ();
        p.putAll (map);

        ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        p.store (baos, "");

        return new ByteArrayInputStream (baos.toByteArray ());
    }

    Map<?, ?> loadDefaults () {
        String handlerClass = TerminalHandler.class.getName ();
        String patternClass = PatternFormat.class.getName ();

        return map (
            entry (".level", INFO.toString ()),
            entry ("handlers", handlerClass),
            entry (handlerClass + ".formatter", patternClass),
            entry (handlerClass + ".level", INFO.toString ())
        );
    }
}
