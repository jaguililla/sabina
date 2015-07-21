package sabina.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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

    public void load (Entry... entries) {

    }

    public void load (InputStream... inputs) {

    }

    public <T> T get (String key, T value) {
        return null;
    }
}
