package co.there4.bali;

import static co.there4.bali.ConsoleInternal.*;
import static java.util.Arrays.*;
import static co.there4.bali.Checks.require;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.stream.IntStream;

/**
 * Utilities to output text in the console (stdout).
 */
public interface Console {
    /**
     * Ansi code with no effect or color is a reset.
     *
     * @param fxs
     * @return
     */
    static String ansi (AnsiEffect... fxs) {
        checkArray (fxs);

        return ansiCode (IntStream.of (), stream (fxs));
    }

    static String ansi (AnsiColor fg, AnsiEffect... fxs) {
        Checks.require (fg != null);
        checkArray (fxs);

        return ansiCode (IntStream.of (fg.fg), stream (fxs));
    }

    static String ansi (AnsiColor fg, AnsiColor bg, AnsiEffect... fxs) {
        Checks.require (fg != null);
        Checks.require (bg != null);
        checkArray (fxs);

        return ansiCode (IntStream.of (fg.fg, bg.bg), stream (fxs));
    }

    static void println (String text, Object... parameters) {
        print (text + "%n", parameters);
    }

    static void print (String text, Object... parameters) {
        System.out.printf (text, parameters);
    }

    static ByteArrayOutputStream redirectOut () {
        ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        redirectOut (baos);
        return baos;
    }

    static void redirectOut (OutputStream out) {
        System.setOut (new PrintStream(out));
    }

    static void restoreOut () {
        System.out.flush ();
        System.setOut (OUT);
    }
}
