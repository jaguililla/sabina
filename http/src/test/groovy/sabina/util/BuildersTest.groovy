package sabina.util

import java.time.LocalDate

import static java.util.Arrays.asList
import static sabina.util.Builders.*

import org.testng.annotations.Test

import static sabina.util.Entry.entry

@Test class BuildersTest {
    private static Map<?, ?> createSampleMap () {
        map (
            entry ("name", "Juanjo"),
            entry ("surname", "Aguililla"),
            entry ("birth", LocalDate.of (1979, 1, 22)),
            entry ("address", map (
                entry ("street", "C/Albufera")
            )),
            entry ("sports", list (
                map (
                    entry ("name", "running"),
                    entry ("active", false)
                ),
                map (
                    entry ("name", "basket"),
                    entry ("active", true)
                ),
                map (
                    entry ("name", "karate"),
                    entry ("active", true)
                )
            )),
            entry ("countdown", list (3, 2, 1, 0))
        )
    }

    @Test (expectedExceptions = IllegalStateException)
    public void "an instance of 'Builders' can not be created" () {
        _create ()
    }

    public void "a set can be built from a sequence of elements and 'nulls' are discarted" () {
        Set<?> s = set ("a", null, "b", null, false)
        assert s.containsAll (asList ("a", "b", false))
    }

    public void "a list can be built from a sequence of elements and 'nulls' are discarted" () {
        List<?> l = list ("a", null, "b", null, false)
        assert false == get (l, 2)
        assert l.equals (asList ("a", "b", false))
        assert !l.equals (asList (false, "b", "a"))
    }

    public void "a map can be built from a sequence of elements and 'nulls' are discarted" () {
        Map<?, ?> l = map (
            entry ("a", "z"),
            null,
            entry ("b", null),
            null,
            entry (null, null),
            null,
            entry ("c", 1)
        )
        assert l.size () == 4
        assert 1 == get (l, "c")
        assert null == get (l, "b")
    }

    public void "any set of 'null' elements results in an empty collection" () {
        assert map (null, null).isEmpty ()
        assert map ((Entry<Object, Object>[])null).isEmpty ()
        assert list (null, null).isEmpty ()
        assert list ((Object[])null).isEmpty ()
        assert set (null, null).isEmpty ()
        assert set ((Object[])null).isEmpty ()
    }

    public void "get methods return the searched node" () {
        Map<?, ?> person = createSampleMap ()

        assert get (person, "sports", 2, "active") == true
        assert get (person, "address", "street").equals ("C/Albufera")
    }

    public void "get methods throws an error when the searched node does not exist" () {
        Map<Object, Object> person = createSampleMap ()

        List<Object []> paths = [
            ["sports", 3, "active"] as Object [],
            ["sports", -1, "active"] as Object [],
            ["sports", 2, "notFound"] as Object [],
            ["sports", "a", "notFound"] as Object []
        ]

        paths.each {
            try {
                get (person, it)
                assert false
            }
            catch (IllegalArgumentException e) {
                assert true
            }
        }
    }
}
