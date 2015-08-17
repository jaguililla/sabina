package sabina.util;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.concat;
import static sabina.util.Checks.checkArgument;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author jamming
 */
public final class Console {
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

        final int code;
        final int fg;
        final int bg;

        AnsiColor (int code) {
            this.code = code;
            this.fg = FOREGROUND + code;
            this.bg = BACKGROUND + code;
        }
    }

    public enum AnsiEffect {
        BOLD (1, true),
        UNDERLINE (4, true),
        BLINK (5, true),
        INVERSE (7, true),

        BOLD_OFF (1, false),
        UNDERLINE_OFF (4, false),
        BLINK_OFF (5, false),
        INVERSE_OFF (7, false);

        private static final int SWITCH_EFFECT = 20;

        final int code;

        AnsiEffect (int code, boolean on) {
            this.code = on? code : code + SWITCH_EFFECT;
        }
    }

    private static final String ANSI_PREFIX = "\u001B[";
    private static final String ANSI_END = "m";
    private static final String ANSI_SEPARATOR = ";";
    private static final String ANSI_RESET = "0";

    private static String ansiCode (IntStream colors, Stream<AnsiEffect> fxs) {
        String body = concat(colors, fxs.mapToInt (fx -> fx.code))
            .mapToObj (String::valueOf)
            .collect (joining (ANSI_SEPARATOR));

        return ANSI_PREFIX + (body.isEmpty ()? ANSI_RESET : body) + ANSI_END;
    }

    private static <T> void checkArray (T[] fxs) {
        checkArgument (fxs != null && !asList (fxs).contains (null));
    }

    /**
     * Ansi code with no effect or color is a reset.
     *
     * @param fxs
     * @return
     */
    public static String ansi (AnsiEffect... fxs) {
        checkArray (fxs);

        return ansiCode (IntStream.of (), stream (fxs));
    }

    public static String ansi (AnsiColor fg, AnsiEffect... fxs) {
        checkArgument (fg != null);
        checkArray (fxs);

        return ansiCode (IntStream.of (fg.fg), stream (fxs));
    }

    public static String ansi (AnsiColor fg, AnsiColor bg, AnsiEffect... fxs) {
        checkArgument (fg != null);
        checkArgument (bg != null);
        checkArray (fxs);

        return ansiCode (IntStream.of (fg.fg, bg.bg), stream (fxs));
    }

    public static void println (String text, Object... parameters) {
        print (text + "%n", parameters);
    }

    public static void print (String text, Object... parameters) {
        System.out.printf (text, parameters);
    }

    static void _create () { new Console (); }

    private Console () {
        throw new IllegalStateException ();
    }
}
