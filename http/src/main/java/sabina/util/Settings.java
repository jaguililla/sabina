package sabina.util;

import static java.util.stream.Collectors.toMap;
import static sabina.util.Builders.entry;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Read only (not allowed to store) settings. If no key is found, default is returned or exception
 * thrown.
 *
 * <p>Simple settings, if you need more use HOCON
 *
 * @author jamming
 */
public final class Settings {
    private static Settings instance;

    public static Settings settings () {
        return instance == null? instance = new Settings () : instance;
    }

    public static Map<String, String> url (String inputs) {
        try {
            return stream (new URL (inputs).openConnection ().getInputStream ());
        }
        catch (IOException e) {
            return new HashMap<> ();
        }
    }

    public static Map<String, String> resource (String inputs) {
        try {
            return stream (Class.class.getResourceAsStream (inputs));
        }
        catch (IOException e) {
            return new HashMap<> ();
        }
    }

    public static Map<String, String> file (String inputs) {
        try {
            return stream (new FileInputStream (inputs));
        }
        catch (IOException e) {
            return new HashMap<> ();
        }
    }

    public static Map<String, String> parameters (String[] inputs) {
        Map<String, String> result = new HashMap<> ();

        for (int ii = 0; ii < inputs.length; ii += 2) {
            String odd = inputs[ii].trim ();

            if (odd.startsWith ("--"))
                odd = odd.substring ("--".length ());
            else
                throw new IllegalArgumentException ();

            String even = inputs[ii + 1].trim ();

            result.put (odd, even);
        }

        return result;
    }

    private static Map<String, String> stream (InputStream stream) throws IOException {
        if (stream == null)
            return new HashMap<> ();

        try (InputStream s = stream) {
            Properties props = new Properties ();
            props.load (s);
            return props.entrySet ().stream ()
                .map (entry ->
                    entry ( (String)entry.getKey (), (String)entry.getValue () )
                )
                .collect (toMap (Entry::getKey, Entry::getValue));
        }
    }

    private Map<String, String> settings = new HashMap<> ();

    private Settings () {
        super ();
    }

    /**
     * Load from: url, file or resource. Finally, it checks system properties with same name to
     * override values (useful to set values in the command line.
     *
     * @param entries .
     */
    @SafeVarargs public final Settings load (Map<String, String>... entries) {
        Arrays.stream (entries).forEach (settings::putAll);
        // Override with system properties (if set)
        return this;
    }

    public Map<String, String> getAll () {
        Map<String, String> result = new HashMap<> ();
        result.putAll (settings);
        return result;
    }

    public String get (String key) {
        return settings.get (key);
    }

    public int getInt (String key) {
        return Integer.parseInt (get (key));
    }

    public long getLong (String key) {
        return Long.parseLong (get (key));
    }

    public byte getByte (String key) {
        return Byte.parseByte (get (key));
    }

    public short getShort (String key) {
        return Short.parseShort (get (key));
    }

    public float getFloat (String key) {
        return Float.parseFloat (get (key));
    }

    public double getDouble (String key) {
        return Double.parseDouble (get (key));
    }
}
