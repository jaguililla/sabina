package co.there4.bali;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.concat;
import static co.there4.bali.Checks.checkArgument;

import java.io.PrintStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

interface ConsoleInternal {
    String ANSI_PREFIX = "\u001B[";
    String ANSI_END = "m";
    String ANSI_SEPARATOR = ";";
    String ANSI_RESET = "0";

    PrintStream OUT = System.out;

    static String ansiCode (IntStream colors, Stream<AnsiEffect> fxs) {
        String body = concat(colors, fxs.mapToInt (fx -> fx.code))
            .mapToObj (String::valueOf)
            .collect (joining (ANSI_SEPARATOR));

        return ANSI_PREFIX + (body.isEmpty ()? ANSI_RESET : body) + ANSI_END;
    }

    static <T> void checkArray (T[] fxs) {
        checkArgument (fxs != null && !asList (fxs).contains (null));
    }
}
