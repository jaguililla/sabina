package sabina.util

import static sabina.util.Things.*

import org.testng.annotations.Test

@Test public class ThingsTest {
    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "print 'null' object throws an exception" () {
        printInstance (null)
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "print object with 'null' fields throws an exception" () {
        printInstance (this, (String[])null)
    }

    public void "print object without fields prints the class" () {
        assert "ThingsTest" == printInstance (this)
    }

    public void "print object with two fields prints the whole instance ignoring empty fields" () {
        assert "ThingsTest {a: 0, b: true}" == printInstance (this,
            printField ("a", 0),
            printField ("b", true),
            printField ("c", null),
            printField ("d", "")
        )
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void "print an empty field name throws an exception" () {
        printField ("", 1)
    }

    public void "print object with hash prints the class and hash code" () {
        assert printInstance (this, true) ==~ /ThingsTest@\d*/
    }

    public void "equal behaves the same as Objects.equals" () {
        assert equal ("str", "str")
        assert equal ("str", "str") == Objects.equals ("str", "str")
    }
}
