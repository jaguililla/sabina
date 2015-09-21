package sabina.util;

import static java.lang.Long.parseLong;
import static java.lang.System.getProperties;
import static java.util.Arrays.stream;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toMap;
import static sabina.util.Builders.entry;
import static sabina.util.Builders.tmap;
import static sabina.util.Checks.checkArgument;
import static sabina.util.Strings.isEmpty;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Read only settings. It is not allowed to store or set parameters, reloading is allowed and
 * replaces old values and adds new parameters (it does not delete parameters).
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
 * <p>TODO Should be like Properties on steroids
 * <p>TODO Resolve paths to load with URIs/URLs (if URL invalid, then load as resource)
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
public final class Configuration {
    private static Configuration instance;

    /**
     _		No
     _1		No
     1_		No
     _1_	No
     1		Yes
     1_0	Yes
     1__0	Yes
     1_0_2	Yes
     1-		No
     -1		Yes
     a		No
     0a		No
     */
    private static final String DEC_RE = "-?\\d+(_*\\d)*";

    /**
     0x_		No
     0x_1		No
     0x1_		No
     0x_1_		No
     0x1		Yes
     0x1_0		Yes
     0x1__0		Yes
     0x1_0_2	Yes
     0x1-		No
     -0x1		Yes
     0xa		Yes
     0x0a		Yes
     0xg		No
     0x0g		No
     */
    private static final String HEX_RE = "-?0x\\p{XDigit}+(_*\\p{XDigit})*";

    /**
     *
     */
    private static final String OCT_RE = "-?0[0-7]+(_*[0-7])*";

    private static final String FLOAT_RE = "\\d+(_*\\d)*";

    private static final Map<Pattern, Function<String, Object>> converters = tmap (
        entry (compileCi (DEC_RE + "y"), v -> Byte.decode (v.substring (v.length () - 1))),
        entry (compileCi ("true|false"), Boolean::valueOf),
        entry (compile ("'.'"), v -> v.charAt (1)), // Unicode is translated by properties
        entry (compile ("\".*\""), v -> v.substring (1, v.length () - 1))
    );

    static final Pattern compileCi (String pattern) {
        return compile (pattern, CASE_INSENSITIVE);
    }

    /**
     * Gets the configuration instance.
     *
     * @return The unique Configuration instance.
     */
    public static Configuration configuration () {
        return instance == null? instance = new Configuration () : instance;
    }

    public static Map<String, String> url (String inputs) {
        try {
            return loadStream (new URL (inputs).openConnection ().getInputStream ());
        }
        catch (IOException e) {
            return new HashMap<> ();
        }
    }

    public static Map<String, String> resource (String inputs) {
        return loadStream (Class.class.getResourceAsStream (inputs));
    }

    public static Map<String, String> file (String inputs) {
        try {
            return loadStream (new FileInputStream (inputs));
        }
        catch (FileNotFoundException e) {
            return new HashMap<> ();
        }
    }

    public static Map<String, String> system (String prefix) {
        checkArgument (!isEmpty (prefix));

        return getProperties ().keySet ().stream ()
            .map (Things::stringOf)
            .filter (it -> it.startsWith (prefix))
            .collect (toMap (it -> it, System::getProperty));
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
        checkArgument (inputs != null && inputs.length % 2 == 0);

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

    static Map<String, String> loadStream (InputStream stream) {
        if (stream == null)
            return new HashMap<> ();

        try (InputStream s = stream) {
            Properties props = new Properties ();
            props.load (s);
            return props.entrySet ().stream ()
                .map (entry ->
                    entry ((String)entry.getKey (), (String)entry.getValue ())
                )
                .collect (toMap (Entry::getKey, Entry::getValue));
        }
        catch (IOException e) {
            return new HashMap<> ();
        }
    }

    private Map<String, Object> settings = new LinkedHashMap<> ();

    private Configuration () {
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
    @SafeVarargs public final Configuration load (Map<String, String>... entries) {
        stream (entries).forEach (settings::putAll);
        settings = settings.entrySet ().stream ()
            .map (this::setType)
            .collect (toMap (Entry::getKey, Entry::getValue));
        return this;
    }

    private Entry<String, Object> setType (Entry<String, Object> entry) {
        return entry; // TODO Convert value to the right type (using scanner?)
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

    public boolean getBoolean (String key) {
        return Boolean.parseBoolean (getString (key));
    }
}
