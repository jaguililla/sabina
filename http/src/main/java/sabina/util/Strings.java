/*
 * Copyright © 2015 Juan José Aguililla. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package sabina.util;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static sabina.util.Checks.checkArgument;
import static sabina.util.Objects.stringOf;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * TODO .
 *
 * @author jam
 */
public final class Strings {
    public enum AnsiColor {
        BLACK (0),
        RED (1),
        GREEN (2),
        YELLOW (3),
        BLUE (4),
        MAGENTA (5),
        CYAN (6),
        WHITE (7),
        DEFAULT (9);

        private static final int FOREGROUND = 30;
        private static final int BACKGROUND = 40;

        public final int code;
        public final int fg;
        public final int bg;

        private AnsiColor (int code) {
            this.code = code;
            this.fg = FOREGROUND + code;
            this.bg = BACKGROUND + code;
        }
    }

    public enum AnsiEffect {
        BOLD (1),
        UNDERLINE (4),
        BLINK (5),
        INVERSE (7);

        public static final int SWITCH_EFFECT = 20;

        final int on;
        final int off;

        private AnsiEffect (int code) {
            this.on = code;
            this.off = SWITCH_EFFECT + code;
        }
    }

    private static final String ANSI_PREFIX = "\u001B[";
    private static final String ANSI_END = "m";

    public static final String ANSI_RESET = ANSI_PREFIX + "0" + ANSI_END;

    public static String ansi (AnsiEffect... fxs) {
        throw new UnsupportedOperationException ();
    }

    public static String ansi (AnsiColor fg, AnsiEffect... fxs) {
        throw new UnsupportedOperationException ();
    }

    public static String ansi (AnsiColor fg, AnsiColor bg, AnsiEffect... fxs) {
        throw new UnsupportedOperationException ();
    }

    public static String ansi (String text, AnsiColor fg, AnsiColor bg, AnsiEffect... fxs) {
        throw new UnsupportedOperationException ();
    }

    /**
     * Calls {@link #filter(String, Entry[])} converting the map in entries.
     *
     * @see #filter(String, Entry[])
     */
    public static String filter (final String text, final Map<?, ?> parameters) {
        checkArgument (parameters != null);
        Set<? extends Entry<?, ?>> entries = parameters.entrySet ();
        return filter (text, entries.toArray (new Entry[entries.size ()]));
    }

    /**
     * Filters a text substituting each key by its value. The keys format is:
     * <code>${key}</code> and all occurrences are replaced by the supplied value.
     *
     * <p>If a variable does not have a parameter, it is left as it is.
     *
     * @param text The text to filter. Can not be 'null'.
     * @param parameters The map with the list of key/value tuples. Can not be 'null'.
     * @return The filtered text or the same string if no values are passed or found in the text.
     */
    public static String filter (final String text, final Entry<?, ?>... parameters) {
        checkArgument (text != null);
        checkArgument (parameters != null);

        String result = text;

        for (Entry<?, ?> parameter : parameters) {
            Object v = parameter.getValue ();
            Object k = parameter.getKey ();
            checkArgument (v != null);
            checkArgument (k != null);

            String key = stringOf (k);
            checkArgument (!isEmpty (key));
            String value = stringOf (v);

            result = result.replace ("${" + key + "}", value);
        }

        return result;
    }

    public static boolean isEmpty (String str) {
        return str == null || str.isEmpty ();
    }

    public static String encode (final byte[] body, final String encoding) {
        return ofNullable (encoding).map (enc -> {
            try {
                return body == null? "" : new String (body, enc);
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException (e);
            }
        }).orElse (new String (body));
    }

    public static byte[] decode (final String text, final String encoding) {
        if (text == null)
            return new byte[0];

        if (encoding == null)
            return text.getBytes ();

        try {
            return text.getBytes (encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException (format ("Error decoding '%s' with '%s'", text, encoding),
                e);
        }
    }

    public static String repeat (String str, int times) {
        StringBuilder sb = new StringBuilder (str.length () * times);

        for (int ii = 0; ii < times; ii++)
            sb.append (str);

        return sb.toString ();
    }

    /**
     * Indenta todas las líneas de una cadena de texto usando una cadena de
     * texto como relleno el número de veces que se indique.
     * @param sourceString Cadena cuyas líneas serán indentadas.
     * @param padString Cadena que se añadirá al principio de cada línea.
     * @param times Número de veces que se añadirá la cadena de relleno.
     * @return Cadena con todas sus líneas indentadas.
     */
    public static String indent (final String sourceString, final String padString, final int times) {
        StringTokenizer lineTokenizer = new StringTokenizer(sourceString, "\n");
        StringBuffer result = new StringBuffer();
        String appendString = repeat (padString, times);
        while (lineTokenizer.hasMoreTokens()) {
            result.append(appendString);
            result.append(lineTokenizer.nextToken());
            result.append('\n');
        }
        return result.toString();
    }

    private Strings () {
        throw new IllegalStateException ();
    }
}
