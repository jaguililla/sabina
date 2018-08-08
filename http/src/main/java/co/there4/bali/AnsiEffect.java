package co.there4.bali;

/**
 * TODO .
 *
 * @author jam
 */
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
