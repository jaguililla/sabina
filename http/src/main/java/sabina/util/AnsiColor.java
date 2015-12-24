package sabina.util;

/**
 * TODO .
 *
 * @author jam
 */
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
