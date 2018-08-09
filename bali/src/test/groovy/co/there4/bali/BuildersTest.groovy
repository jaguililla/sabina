package co.there4.bali

import java.time.LocalDate

import static java.util.Arrays.asList
import static Builders.*

import org.testng.annotations.Test

@Test class BuildersTest {
    private static Map<?, ?> createSampleMap () {
        mapOf (
            entry ("name", "Juanjo"),
            entry ("surname", "Aguililla"),
            entry ("birth", LocalDate.of (1979, 1, 22)),
            entry ("address", mapOf (
                entry ("street", "C/Albufera")
            )),
            entry ("sports", listOf (
                mapOf (
                    entry ("name", "running"),
                    entry ("active", false)
                ),
                mapOf (
                    entry ("name", "basket"),
                    entry ("active", true)
                ),
                mapOf (
                    entry ("name", "karate"),
                    entry ("active", true)
                )
            )),
            entry ("countdown", listOf (3, 2, 1, 0))
        )
    }

    @Test void "a set can be built from a sequence of elements and 'nulls' are discarted" () {
        Set<?> s = setOf ("a", null, "b", null, false)
        assert s.containsAll (asList ("a", "b", false))
    }

    @Test void "a list can be built from a sequence of elements and 'nulls' are discarted" () {
        List<?> l = listOf ("a", null, "b", null, false)
        assert false == get (l, 2)
        assert l == asList ("a", "b", false)
        assert l != asList (false, "b", "a")
    }

    @Test void "a map can be built from a sequence of elements and 'nulls' are discarted" () {
        Map<?, ?> l = mapOf (
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

    @Test void "a typed map can be created with 'tmap'" () {
        Map<String, Integer> l = typedMapOf (
            entry ("a", 1),
            null,
            entry ("b", null),
            null,
            entry (null, null),
            null,
            entry ("c", 2)
        )

        assert l.size () == 4
        assert 1 == get (l, "a")
        assert null == get (l, "b")
        assert 2 == get (l, "c")
    }

    @Test void "any set of 'null' elements results in an empty collection" () {
        assert mapOf (null, null).isEmpty ()
        assert mapOf ((Map.Entry<Object, Object>[])null).isEmpty ()
        assert listOf (null, null).isEmpty ()
        assert listOf ((Object[])null).isEmpty ()
        assert setOf (null, null).isEmpty ()
        assert setOf ((Object[])null).isEmpty ()
    }

    @Test void "get methods return the searched node" () {
        Map<?, ?> person = createSampleMap ()

        assert get (person, "sports", 2, "active") == true
        assert get (person, "address", "street").equals ("C/Albufera")
    }

    @Test void "get methods throws an error when the searched node does not exist" () {
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
