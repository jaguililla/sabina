package sabina.util

import org.testng.annotations.Test

import java.util.stream.Stream

import static java.util.stream.Collectors.toList

@Test public class FunctionalTest implements Functional, Builders {
    public void "some returns the correct optional" () {
        assert some (null) == Optional.empty ()
        assert some ("string") == Optional.of ("string")
        assert some (1) == Optional.of (1)
    }

    public void "stream of 'null' returns empty stream" () throws Exception {
        assert streamOf (null).collect (toList ()) == Stream.of ().collect (toList ())
    }

    public void "stream of many elements return the correct stream" () throws Exception {
        assert streamOf (null, "alfa", null, "bravo").collect (toList ()) ==
            Stream.of (null, "alfa", null, "bravo").collect (toList ())
    }

    public void "stream of entries elements return the correct map" () throws Exception {
        assert asMap (
            null,
            entry ("alfa", "a"),
            null,
            entry ("bravo", 1)
        ) == map (
            entry ("alfa", "a"),
            entry ("bravo", 1),
        )
    }

    public void "typed stream of entries elements return the correct map" () throws Exception {
        assert asTMap (
            null,
            entry ("alfa", "a"),
            null,
            entry ("bravo", "b")
        ) == tmap (
            entry ("alfa", "a"),
            entry ("bravo", "b"),
        )
    }
}
