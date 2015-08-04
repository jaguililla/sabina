package sabina.util

import org.testng.annotations.Test

import static sabina.util.Builders.entry
import static sabina.util.Strings.filter

@Test
public class StringsTest {
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
        filter ("text", entry (null, 1))
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "filter does not allow empty variable keys" () {
        filter ("text", entry ("", 1))
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "filter does not allow 'null' variable values" () {
        filter ("text", entry ("key", null))
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
}
