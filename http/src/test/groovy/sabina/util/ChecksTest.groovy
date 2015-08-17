package sabina.util

import org.testng.annotations.Test

import static sabina.util.Checks.*

@Test class ChecksTest {
    @Test (expectedExceptions = IllegalStateException)
    public void "an instance of 'Checks' can not be created" () {
        _create ()
    }
}
