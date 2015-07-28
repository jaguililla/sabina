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
import static sabina.util.Builders.entry;

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
        INVERSE (7),

        RESET (0);

        public static final int SWITCH_EFFECT = 20;

        final int on;

        private AnsiEffect (int code) {
            this.on = code;
        }
    }

    public static final String ANSI_PREFIX = "\u001B[";

    public static String ansi (AnsiColor fg, AnsiEffect... fxs) {
        return "";
    }

    public static String ansi (AnsiColor fg, AnsiColor bg, AnsiEffect... fxs) {
        return "";
    }

    public static String ansi (String text, AnsiColor fg, AnsiColor bg, AnsiEffect... fxs) {
        return text;
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static boolean isNullOrEmpty (String str) {
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

    public static String filter (final String text, Map<?, ?> parameters) {
        Set<? extends Entry<?, ?>> entries = parameters.entrySet ();
        return filter (text, entries.toArray (new Entry[entries.size ()]));
    }

    /**
     * ${key}
     *
     * @param text
     * @param parameters
     * @return
     */
    public static String filter (final String text, final Entry<?, ?>... parameters) {
        String result = text;

        for (Entry<?, ?> parameter : parameters) {
            String key = String.valueOf (parameter.getKey ());
            String value = String.valueOf (parameter.getValue ());
            result = result.replace ("${" + key + "}", value);
        }

        return result;
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

    public static void main (String... args) {
        System.out.println (
            filter ("${abc} ${b}",
                entry ("abc", "1"),
                entry ("b", "2")
            )
        );
    }
}
