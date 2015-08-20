package sabina.util

import org.testng.annotations.Test

import static sabina.util.Console.*
import static sabina.util.Console.AnsiColor.*
import static sabina.util.Console.AnsiEffect.*
import static sabina.util.Strings.EOL

@Test class ConsoleTest {
    @Test (expectedExceptions = IllegalStateException)
    public void "an instance of 'Console' can not be created" () {
        _create ()
    }

    @Test (expectedExceptions = IllegalArgumentException)
    public void "ansi with explicit 'null' throws an exception" () {
        ansi ((AnsiEffect[])null)
    }

    @Test (expectedExceptions = IllegalArgumentException)
    public void "ansi with a 'null' effect throws an exception" () {
        ansi (BOLD, null)
    }

    public void "ansi code without elements returns ansi reset code" () {
        assert ansi ().equals ("\u001B[0m")
    }

    public void "ansi code with a single effect has the proper format" () {
        assert ansi (BOLD).equals ("\u001B[1m")
        assert ansi (BOLD_OFF).equals ("\u001B[21m")
    }

    public void "ansi code with two effects has the proper format" () {
        assert ansi (BOLD, UNDERLINE_OFF).equals ("\u001B[1;24m")
        assert ansi (BLINK_OFF, INVERSE_OFF).equals ("\u001B[25;27m")
    }

    @Test (expectedExceptions = IllegalArgumentException)
    public void "ansi code with 'null' foreground throws an exception" () {
        assert ansi ((AnsiColor)null)
    }

    @Test (expectedExceptions = IllegalArgumentException)
    public void "ansi code with 'null' foreground and a background throws an exception" () {
        assert ansi ((AnsiColor)null, RED)
    }

    @Test (expectedExceptions = IllegalArgumentException)
    public void "ansi code with 'null' background throws an exception" () {
        assert ansi (DEFAULT, (AnsiColor)null)
    }

    public void "ansi code with foreground and effects returns the correct code" () {
        assert ansi (RED, BOLD, UNDERLINE).equals ("\u001B[31;1;4m")
    }

    public void "ansi code with foreground, background and effects returns the correct code" () {
        assert ansi (RED, BLACK, BLINK, INVERSE).equals ("\u001B[31;40;5;7m")
    }

    @Test (description = "Only to show the output in a console and check visually")
    public void "rainbow table is printed nicely" () {
        println (" %8s | %-8s", "FORE", "BACK")

        for (AnsiColor bg : AnsiColor.values ()) {
            for (AnsiColor fg : AnsiColor.values ())
                print (" %s%8s | %-8s%s", ansi (fg, bg), fg, bg, ansi ())

            println ()
        }

        println "Back to normal"
    }

    @Test (description = "Only to show the output in a console and check visually")
    public void "effects and foreground color table" () {
        println (" %14s | %-14s", "FOREGROUND", "EFFECT")

        for (AnsiColor fg : AnsiColor.values ()) {
            print (" %s%14s | %-14s%s", ansi (fg), fg, "NONE", ansi ())
            for (AnsiEffect fx : EnumSet.of (BOLD, UNDERLINE, BLINK, INVERSE))
                print (" %s%14s | %-14s%s", ansi (fg, fx), fg, fx, ansi ())

            println ()
        }

        println "Back to normal"
    }

    public void "print formats the message correctly on system output" () {
        ByteArrayOutputStream baos = redirectOut ()

        println ("%s string %d integer %1.2f float", "str", 12, 1.2);

        restoreOut ()
        assert baos.toString ().equals ("str string 12 integer 1.20 float" + EOL)
    }
}
