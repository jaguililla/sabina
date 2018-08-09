package co.there4.bali

import org.testng.annotations.Test

import java.util.stream.Stream

import static java.util.stream.Collectors.toList

@Test class FunctionalTest implements Functional, Builders {
    @Test void "some returns the correct optional" () {
        assert some (null) == Optional.empty ()
        assert some ("string") == Optional.of ("string")
        assert some (1) == Optional.of (1)
    }

    @Test void "stream of 'null' returns empty stream" () throws Exception {
        assert streamOf (null).collect (toList ()) == Stream.of ().collect (toList ())
    }

    @Test void "stream of many elements return the correct stream" () throws Exception {
        assert streamOf (null, "alfa", null, "bravo").collect (toList ()) == Stream
            .of (null, "alfa", null, "bravo").collect (toList ())
    }

    @Test void "stream of entries elements return the correct map" () throws Exception {
        assert asMap (
            null,
            entry ("alfa", "a"),
            null,
            entry ("bravo", 1)
        ) == mapOf (
            entry ("alfa", "a"),
            entry ("bravo", 1),
        )
    }

    @Test void "typed stream of entries elements return the correct map" () throws Exception {
        assert asTMap (
            null,
            entry ("alfa", "a"),
            null,
            entry ("bravo", "b")
        ) == typedMapOf (
            entry ("alfa", "a"),
            entry ("bravo", "b"),
        )
    }
}
