package co.there4.bali

import org.testng.annotations.Test

import static Console.*
import static AnsiColor.RED
import static AnsiColor.BLACK
import static AnsiColor.DEFAULT
import static AnsiEffect.*
import static Strings.EOL

@Test class ConsoleTest {
    @Test (expectedExceptions = IllegalArgumentException)
    void "ansi with explicit 'null' throws an exception" () {
        ansi ((AnsiEffect[])null)
    }

    @Test (expectedExceptions = IllegalArgumentException)
    void "ansi with a 'null' effect throws an exception" () {
        ansi (BOLD, null)
    }

    @Test void "ansi code without elements returns ansi reset code" () {
        assert ansi () == "\u001B[0m"
    }

    @Test void "ansi code with a single effect has the proper format" () {
        assert ansi (BOLD) == "\u001B[1m"
        assert ansi (BOLD_OFF) == "\u001B[21m"
    }

    @Test void "ansi code with two effects has the proper format" () {
        assert ansi (BOLD, UNDERLINE_OFF) == "\u001B[1;24m"
        assert ansi (BLINK_OFF, INVERSE_OFF) == "\u001B[25;27m"
    }

    @Test (expectedExceptions = IllegalArgumentException)
    void "ansi code with 'null' foreground throws an exception" () {
        assert ansi ((AnsiColor)null)
    }

    @Test (expectedExceptions = IllegalArgumentException)
    void "ansi code with 'null' foreground and a background throws an exception" () {
        assert ansi ((AnsiColor)null, RED)
    }

    @Test (expectedExceptions = IllegalArgumentException)
    void "ansi code with 'null' background throws an exception" () {
        assert ansi (DEFAULT, (AnsiColor)null)
    }

    @Test void "ansi code with foreground and effects returns the correct code" () {
        assert ansi (RED, BOLD, UNDERLINE) == "\u001B[31;1;4m"
    }

    @Test void "ansi code with foreground, background and effects returns the correct code" () {
        assert ansi (RED, BLACK, BLINK, INVERSE) == "\u001B[31;40;5;7m"
    }

    @Test (description = "Only to show the output in a console and check visually")
    void "rainbow table is printed nicely" () {
        println (" %8s | %-8s", "FORE", "BACK")

        for (AnsiColor bg : AnsiColor.values ()) {
            for (AnsiColor fg : AnsiColor.values ())
                print (" %s%8s | %-8s%s", ansi (fg, bg), fg, bg, ansi ())

            println ()
        }

        println "Back to normal"
    }

    @Test (description = "Only to show the output in a console and check visually")
    void "effects and foreground color table" () {
        println (" %14s | %-14s", "FOREGROUND", "EFFECT")

        for (AnsiColor fg : AnsiColor.values ()) {
            print (" %s%14s | %-14s%s", ansi (fg), fg, "NONE", ansi ())
            for (AnsiEffect fx : EnumSet.of (BOLD, UNDERLINE, BLINK, INVERSE))
                print (" %s%14s | %-14s%s", ansi (fg, fx), fg, fx, ansi ())

            println ()
        }

        println "Back to normal"
    }

    @Test void "print formats the message correctly on system output" () {
        ByteArrayOutputStream baos = redirectOut ()

        println ("%s string %d integer %1.2f float", "str", 12, 1.2)

        restoreOut ()
        assert baos.toString () == "str string 12 integer 1.20 float" + EOL
    }
}
