package sabina.util

import org.testng.annotations.Test

import static sabina.util.Builders.entry
import static sabina.util.Strings.filter

@Test
public class SettingsTest {
    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "filter does not allow 'null' text" () {
        filter (null)
    }

    public void "filter replaces all occurences of variables with their values" () {

        String result = filter ('${email}: User ${user} aka ${user} <${email}>',
            entry ('user', 'John'),
            entry ('email', 'john@example.co')
        )

        assert result.equals ('john@example.co: User John aka John <john@example.co>')
    }
}
