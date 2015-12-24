package sabina.util;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.concat;
import static sabina.util.Checks.checkArgument;
import static sabina.util.ConsoleInternal.OUT;
import static sabina.util.ConsoleInternal.ansiCode;
import static sabina.util.ConsoleInternal.checkArray;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author jamming
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
        checkArgument (fg != null);
        checkArray (fxs);

        return ansiCode (IntStream.of (fg.fg), stream (fxs));
    }

    static String ansi (AnsiColor fg, AnsiColor bg, AnsiEffect... fxs) {
        checkArgument (fg != null);
        checkArgument (bg != null);
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
