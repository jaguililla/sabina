package co.there4.bali;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static co.there4.bali.Checks.checkArgument;
import static co.there4.bali.Strings.isEmpty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.StringJoiner;

public interface Io {
    ClassLoader classLoader = currentThread ().getContextClassLoader ();

    static String read (String input) {
        checkArgument (!isEmpty (input));
        InputStream stream = classLoader.getResourceAsStream (input);
        checkArgument (stream != null, format ("Resource '%s' not found", input));
        return read (stream);
    }

    static String read (InputStream input) {
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

    static String read (URL url) {
        try {
            return read (url.openConnection ().getInputStream ());
        }
        catch (IOException e) {
            throw new RuntimeException (e);
        }
    }
}
