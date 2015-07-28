package sabina.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringJoiner;

public final class Io {
    private static ClassLoader classLoader = ClassLoader.getSystemClassLoader ();

    public static ClassLoader classLoader () {
        return classLoader;
    }

    public static void classLoader (Class<?> clazz) {
        classLoader (clazz.getClassLoader ());
    }

    public static void classLoader (ClassLoader classLoader) {
        if (classLoader == null)
            throw new IllegalArgumentException ();

        Io.classLoader = classLoader;
    }

    public static String read (String input) throws IOException {
        return read (classLoader.getResourceAsStream (input));
    }

    public static String read (InputStream input) throws IOException {
        BufferedReader br = new BufferedReader (new InputStreamReader (input));
        StringJoiner text = new StringJoiner ("\n");
        for (String line = br.readLine (); line != null; line = br.readLine ())
            text.add (line);
        return text.toString ();
    }

    private Io () {
        throw new IllegalStateException ();
    }
}
