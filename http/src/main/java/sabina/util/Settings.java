package sabina.util;

import static java.lang.Long.parseLong;
import static java.util.stream.Collectors.toMap;
import static sabina.util.Entry.entry;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Read only settings. It is not allowed to store or set parameters, reloading is allowed and
 * replaces and adds new parameters.
 *
 * <p>If no key is found, an exception is thrown.
 *
 * <p>When returning all parameters, a copy is provided, it is NOT MODIFICABLE.
 *
 * <p>Simple settings, if you need more use HOCON.
 *
 * <p>It is a singleton shared by all JVM objects.
 *
 * <p>Usage will be load one time at start scenario, you can build a reload settings upon this
 * class.
 *
 * <p>Values are stored as strings and converted each time they are accessed.
 * TODO Think on how to fix this (trying with patterns at loading time: integers, floats, boolean)
 *      byte b = 0x1A;
 *      short s = 1;
 *      int i = 0b01_000_1;
 *      long l = 1_000_000;
 *
 *      float f = .1_2F;
 *      double d = .1_2d;
 *
 *      boolean bool = true;
 *
 *      char c = '\u0000';
 *      String str = "";
 *
 * @author jamming
 */
public final class Settings {
    private static Settings instance;

    /**
     * Gets the settings instance.
     *
     * @return The unique Settings instance.
     */
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

    public static Map<String, String> system (String prefix) {
        throw new UnsupportedOperationException ();
    }

    /**
     * GNU non standard long parameters supported only
     *
     * --paramName "param value"
     *
     * @param inputs .
     * @return .
     */
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

    private Map<String, Object> settings = new HashMap<> ();

    private Settings () {
        super ();
    }

    /**
     * Load from: url, file or resource. Finally, it checks system properties with same name to
     * override values (useful to set values in the command line.
     *
     * Load from different sources priority is developer responsability. It makes sense to be
     *
     * 1. Application resource (basic defaults)
     * 2. URL shared config accross different systems (architecture)
     * 3. System properties (could be system wide)
     * 4. Config file (installation configuration)
     * 5. Program parameters (specified at application startup)
     *
     * @param entries .
     */
    @SafeVarargs public final Settings load (Map<String, String>... entries) {
        Arrays.stream (entries).forEach (settings::putAll);
        return this;
    }

    public Set<String> keys () {
        return settings.keySet ();
    }

    @SuppressWarnings ("unchecked") public <T> T get (String key) {
        return (T)settings.get (key);
    }

    public String getString (String key) {
        return settings.get (key).toString ();
    }

    public int getInt (String key) {
        return Integer.parseInt (getString (key));
    }

    public long getLong (String key) {
        return parseLong (getString (key));
    }

    public byte getByte (String key) {
        return Byte.parseByte (getString (key));
    }

    public short getShort (String key) {
        return Short.parseShort (getString (key));
    }

    public float getFloat (String key) {
        return Float.parseFloat (getString (key));
    }

    public double getDouble (String key) {
        return Double.parseDouble (getString (key));
    }
}
