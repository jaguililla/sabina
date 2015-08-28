package sabina.util;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static sabina.util.Checks.checkArgument;
import static sabina.util.Strings.isEmpty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.StringJoiner;

/**
 * TODO Check: Thread.currentThread ().getContextClassLoader ()
 */
public final class Io {
    private static ClassLoader classLoader = currentThread ().getContextClassLoader ();

    public static ClassLoader classLoader () {
        return classLoader;
    }

    public static void classLoader (Class<?> clazz) {
        checkArgument (clazz != null);
        classLoader (clazz.getClassLoader ());
    }

    public static void classLoader (ClassLoader classLoader) {
        checkArgument (classLoader != null);
        Io.classLoader = classLoader;
    }

    public static String read (String input) {
        checkArgument (!isEmpty (input));
        InputStream stream = classLoader.getResourceAsStream (input);
        checkArgument (stream != null, format ("Resource '%s' not found", input));
        return read (stream);
    }

    public static String read (InputStream input) {
        checkArgument (input != null);
        BufferedReader br = new BufferedReader (new InputStreamReader (input));
        StringJoiner text = new StringJoiner ("\n");
        try {
            for (String line = br.readLine (); line != null; line = br.readLine ())
                text.add (line);
        }
        catch (IOException e) {
            throw new RuntimeException (e);
        }
        return text.toString ();
    }

    public static String read (URL url) {
        try {
            return read (url.openConnection ().getInputStream ());
        }
        catch (IOException e) {
            throw new RuntimeException (e);
        }
    }

    static void _create () { new Io (); }

    private Io () {
        throw new IllegalStateException ();
    }
}
