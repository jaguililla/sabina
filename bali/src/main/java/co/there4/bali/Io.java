package co.there4.bali;

import static java.lang.String.format;
import static co.there4.bali.Checks.require;
import static co.there4.bali.Strings.isEmpty;

import java.io.*;
import java.net.URL;
import java.util.StringJoiner;

public interface Io {
    ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader ();

    static String read (String input) {
        Checks.require (!isEmpty (input));
        InputStream stream = SYSTEM_CLASS_LOADER.getResourceAsStream (input);
        require (stream != null, format ("Resource '%s' not found", input));
        return read (stream);
    }

    static String read (InputStream input) {
        Checks.require (input != null);
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

    static String read (URL url) {
        try {
            return read (url.openConnection ().getInputStream ());
        }
        catch (IOException e) {
            throw new RuntimeException (e);
        }
    }

    static byte[] readInput(final InputStream input) {
        return Unchecked.get (() -> {
            final DataInputStream dataInput = input instanceof DataInputStream?
                (DataInputStream)input : new DataInputStream(input);

            final byte[] bytes = new byte[dataInput.available()];
            dataInput.readFully(bytes);
            return bytes;
        });
    }

    static InputStream getResourceStream (final String path) {
        return SYSTEM_CLASS_LOADER.getResourceAsStream (path);
    }
}
