package sabina.util.log;

import static java.util.logging.LogManager.getLogManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

public class LogSettings {
    public static void load () {
        // Reload configuration
    }

    public LogSettings () throws IOException {
        InputStream is = new ByteArrayInputStream ("".getBytes ());
        getLogManager ().readConfiguration (is);
    }
}
