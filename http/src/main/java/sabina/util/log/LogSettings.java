package sabina.util.log;

import static java.util.logging.Level.FINE;
import static java.util.logging.LogManager.getLogManager;
import static java.util.stream.Collectors.toMap;
import static sabina.util.Entry.entry;
import static sabina.util.Configuration.configuration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import sabina.util.Entry;

/**
 * Load log settings from Configuration. If no settings with 'logging' prefix are found a default
 * configuration is loaded.
 *
 * Any errors in the logging config throws a runtime exception. It may stop the app, that's
 * probably what you want to do if you start with bad settings.
 *
 */
public final class LogSettings {
    /**
     * To avoid calling 'new LogSettings ()' in code. Who the hell designed Java logging?
     */
    public static void load () {
        new LogSettings ();
    }

    public LogSettings () {
        try {
            Map<String, String> loggingSettings = loadLoggingSettings ();

            InputStream is = mapToStream (loggingSettings.isEmpty ()?
                loadDefaults () : loggingSettings);

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

    Map<String, String> loadLoggingSettings () {
        return configuration ().keys ().stream ()
            .filter (k -> k.startsWith ("logging."))
            .map (k -> k.substring ("logging.".length ()))
            .map (k -> entry (
                k.equals ("level")? ".level" : k,
                configuration ().getString ("logging." + k)
            ))
            .collect (toMap (Entry::getKey, Entry::getValue));
    }

    Map<String, String> loadDefaults () {
        Map<String, String> defaults = new HashMap<> (2);
        defaults.put (".level", FINE.toString ());
        defaults.put ("handlers", TerminalHandler.class.getName ());
        defaults.put ("sabina.util.log.TerminalHandler.formatter", PatternFormat.class.getName ());
        return defaults;
    }
}
