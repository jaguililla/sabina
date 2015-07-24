package sabina.util;

import static sabina.util.Builders.entry;
import static sabina.util.Strings.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Read only (not allowed to store) settings. If no key is found, default is returned or exception
 * thrown.
 *
 * @author jamming
 */
public final class Settings {
    private static Settings instance;

    public static Settings settings () {
        return instance == null? instance = new Settings () : instance;
    }

    public static Map<String, ?> url (String inputs) {
        try {
            return stream (new URL (inputs).openConnection ().getInputStream ());
        }
        catch (IOException e) {
            return new HashMap<> ();
        }
    }

    public static Map<String, ?> resource (String inputs) {
        try {
            return stream (Class.class.getResourceAsStream (inputs));
        }
        catch (IOException e) {
            return new HashMap<> ();
        }
    }

    public static Map<String, ?> file (String inputs) {
        try {
            return stream (new FileInputStream (inputs));
        }
        catch (IOException e) {
            return new HashMap<> ();
        }
    }

    private static Object cast (String value) {
        Scanner scanner = new Scanner (value);
        if (scanner.hasNextDouble ()) return scanner.nextDouble ();
        if (scanner.hasNextLong ()) return scanner.nextLong ();
        if (scanner.hasNextBoolean ()) return scanner.nextBoolean ();
        if (scanner.hasNext ()) return scanner.next ();
        throw new IllegalArgumentException ();
    }

    private static Map<String, ?> stream (InputStream stream) throws IOException {
        if (stream == null)
            return new HashMap<> ();

        try (InputStream s = stream) {
            Properties props = new Properties ();
            props.load (s);
            return props.entrySet ().stream ()
                .map (entry ->
                    entry (
                        String.valueOf (entry.getKey ()),
                        cast (String.valueOf (entry.getValue ()))
                    )
                )
                .collect (Collectors.toMap (Entry::getKey, Entry::getValue));
        }
    }

    private Map<String, Object> settings = new HashMap<> ();

    private Settings () {
        super ();
    }

    /**
     * Load from: url, file or resource. Finally, it checks system properties with same name to
     * override values (useful to set values in the command line.
     *
     * @param entries .
     */
    @SafeVarargs public final Settings load (Map<String, ?>... entries) {
        Arrays.stream (entries).forEach (settings::putAll);
        // Override with system properties (if set)
        return this;
    }

    @SuppressWarnings ("unchecked") public <T> T get (String key) {
        return (T)settings.get (key);
    }

    public static void main (String... args) throws Exception {
        settings ().load (
            resource ("/sabina.properties"),
            resource ("/application.properties"),
            file ("application.properties")
        );

        int port = settings ().get ("sabina_default_port");

        System.out.println (port);

        System.out.format (
            "%sRED %sGREEN %sBLUE %sNormal again", ANSI_RED, ANSI_GREEN, ANSI_BLUE, ANSI_RESET
        );
    }
}
