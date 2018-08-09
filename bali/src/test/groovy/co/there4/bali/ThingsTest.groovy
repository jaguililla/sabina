package co.there4.bali

import static Things.*

import org.testng.annotations.Test

@Test class ThingsTest {
    @Test (expectedExceptions = IllegalArgumentException.class)
    void "print 'null' object throws an exception" () {
        printInstance (null)
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    void "print object with 'null' fields throws an exception" () {
        printInstance (this, (String[])null)
    }

    @Test void "print object without fields prints the class" () {
        assert "ThingsTest" == printInstance (this)
    }

    @Test void "print object with two fields prints the whole instance ignoring empty fields" () {
        assert "ThingsTest {a: 0, b: true}" == printInstance (this,
            printField ("a", 0),
            printField ("b", true),
            printField ("c", null),
            printField ("d", "")
        )
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    void "print an empty field name throws an exception" () {
        printField ("", 1)
    }

    @Test void "print object with hash prints the class and hash code" () {
        assert printInstance (this, true) ==~ /ThingsTest@\d*/
    }

    @Test void "equal behaves the same as Objects.equals" () {
        assert equal ("str", "str")
        assert equal ("str", "str") == Objects.equals ("str", "str")
    }
}
