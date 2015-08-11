package sabina.util

import org.testng.annotations.Test

import static sabina.util.Entry.entry
import static sabina.util.Strings.*

@Test public class StringsTest {
    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "filter does not allow 'null' text" () {
        filter (null)
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "filter does not allow 'null' entries" () {
        filter ("text", (Entry<?, ?>[])null)
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "filter does not allow 'null' parameters" () {
        filter ("text", (Map<?, ?>)null)
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "filter does not allow 'null' variable keys" () {
        filter ("text", entry (null, true))
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "filter does not allow empty variable keys" () {
        try {
            filter ("text", entry ("", 1))
        }
        catch (IllegalArgumentException e) {
            assert e.getMessage () == "key with '1' value is empty"
            throw e
        }
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "filter does not allow 'null' variable values" () {
        try {
            filter ("text", entry ("key", null))
        }
        catch (IllegalArgumentException e) {
            assert e.getMessage () == "'key' value is 'null'"
            throw e
        }
    }

    public void "filter returns the given string if no parameters are set" () {

        String template = 'User ${user}'

        assert filter (template).equals (template)
        assert filter (template, new HashMap<>()).equals (template)
    }

    public void "filter returns the same string if no variables are defined in it" () {

        String template = 'User no vars'

        assert filter (template).equals (template)
        assert filter (template, entry ("vars", "value")).equals (template)
        assert filter (template, new HashMap<>()).equals (template)
    }

    public void "filter returns the same string if variable values are not found" () {

        String template = 'User ${user}'

        assert filter (template, entry ("key", "value")).equals (template)
    }

    public void "filter replaces all occurences of variables with their values" () {

        String result = filter ('${email}: User ${user} aka ${user} <${email}>',
            entry ('user', 'John'),
            entry ('email', 'john@example.co')
        )

        assert result.equals ('john@example.co: User John aka John <john@example.co>')
    }

    @Test (expectedExceptions = RuntimeException.class)
    public void "encode any value with invalid encoding throws a runtime exception" () {
        encode ([ 65 ] as byte[], "iso-9999-9999")
    }

    public void "encode a byte array with non ascii characters returns a proper text" () {
        assert "ñ" == encode ([ 0xF1, ] as byte[], "iso-8859-1")
    }

    @Test (expectedExceptions = RuntimeException.class)
    public void "decode any text with invalid encoding throws a runtime exception" () {
        decode ("A", "iso-9999-9999")
    }

    public void "decode an string with non ascii characters returns the correct byte values" () {
        assert [ 0xF1 ] as byte[] == decode ("ñ", "iso-8859-1")
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "is not possible to repeat a 'null' string" () {
        repeat (null, 1);
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "a text can not be repeated a negative number of times" () {
        repeat ("a", -1);
    }

    public void "any string repeated 0 times results in an empty string" () {
        assert "" == repeat ("abc", 0)
    }

    public void "an empty string repeated any number of times results in... an empty string" () {
        assert "" == repeat ("", 10)
    }

    public void "a text repeated n times results in a string with itself repeated n times" () {
        assert "123" == repeat ("123", 1)
        assert "ababab" == repeat ("ab", 3)
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "indenting a null text results in an exception" () {
        indent (null, " ", 1)
    }

    public void "indenting an empty string results in the padding text only" () {
        assert "a a " == indent ("", "a ", 2)
    }

    public void "indenting a single line add the padding and do not add extra new lines" () {
        assert "· · text" == indent ("text", "· ", 2)
    }

    public void "indenting multiple lines add the padding to each of them" () {
        String text =
            """
            line 1

            line 2
            """.stripIndent ()

        String result =
            """            >>>>
            >>>>line 1
            >>>>
            >>>>line 2
            >>>>""".stripIndent ()

        assert result == indent (text, ">>", 2)

        text =
            """            line 1

            line 2""".stripIndent ()

        result =
            """            >>>>line 1
            >>>>
            >>>>line 2""".stripIndent ()

        assert result == indent (text, ">>", 2)
    }
}
