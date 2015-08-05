package sabina.util;

import static sabina.util.Checks.checkArgument;

/**
 * @author jamming
 */
public class Console {
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
        if (fxs.length == 0)
            return ANSI_RESET;
        throw new UnsupportedOperationException ();
    }

    public static String ansi (AnsiColor fg, AnsiEffect... fxs) {
        throw new UnsupportedOperationException ();
    }

    public static String ansi (AnsiColor fg, AnsiColor bg, AnsiEffect... fxs) {
        throw new UnsupportedOperationException ();
    }

    public static String ansi (String text, AnsiColor fg, AnsiColor bg, AnsiEffect... fxs) {
        checkArgument (text != null);
        checkArgument (fxs != null);

        throw new UnsupportedOperationException ();
    }

    public static void println (String text, Object... parameters) {
        print (text + "%n", parameters);
    }

    public static void print (String text, Object... parameters) {
        System.out.printf (text, parameters);
    }
}
