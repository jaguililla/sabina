package sabina.util;

import static java.util.Arrays.stream;
import static sabina.util.Strings.*;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

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

    private Map<String, ?> settings = new HashMap<> ();

    private Settings () {
        super ();
    }

    public void loadArgs (String... entries) {

    }

    /**
     * Load from: url, file or resource. Finally, it checks system properties with same name to
     * override values (useful to set values in the command line.
     *
     * @param inputs
     */
    public void load (String... inputs) {
        stream (inputs).map (in -> {
            try {
                URL uri = new URL (in);

                Properties p = new Properties ();
                p.load (uri.openConnection ().getInputStream ());
                return p.entrySet ();
            }
            catch (IOException e) {
                e.printStackTrace ();
                // Try other options
                return null;
            }
        });
    }

    private Object cast (String value) {
        Scanner scanner = new Scanner (value);
        if (scanner.hasNextDouble ()) return scanner.nextDouble ();
        if (scanner.hasNextLong ()) return scanner.nextLong ();
        if (scanner.hasNextBoolean ()) return scanner.nextBoolean ();
        if (scanner.hasNext ()) return scanner.next ();
        throw new IllegalArgumentException ();
    }

    public <T> T get (String key, T value) {
        return (T)settings.get (key);
    }

    public static void main (String... args) {
        System.out.format (
            "%sRED %sGREEN %sBLUE %sNormal again", ANSI_RED, ANSI_GREEN, ANSI_BLUE, ANSI_RESET
        );
    }
}
