package sabina.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Io {
    public static String read (InputStream input) throws IOException {
        BufferedReader br = new BufferedReader (new InputStreamReader (input));
        StringBuilder text = new StringBuilder ();
        for (String line = br.readLine (); line != null; line = br.readLine ())
            text.append (line);
        return text.toString ();
    }

    private Io () {
        throw new IllegalStateException ();
    }
}
