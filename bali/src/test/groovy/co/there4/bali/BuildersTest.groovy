package co.there4.bali

import java.time.LocalDate
import java.util.stream.Stream

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
    private static Map<?, ?> createSampleMapAlternative () {
        mapOf (
            entry ("name", "Juanjo"),
            entry ("surname", "Aguililla"),
            entry ("birth", LocalDate.of (1979, 1, 22)),
            entryMap ("address",
                entry ("street", "C/Albufera")
            ),
            entryList ("sports",
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
            ),
            entryList ("countdown", 3, 2, 1, 0)
        )
    }

    @Test void "Nested entries works correctly" () {
        assert createSampleMap () == createSampleMapAlternative ()
    }

    @Test void "A set can be built from a sequence of elements and 'nulls' are discarted" () {
        Set<?> aSet = setOf ("a", null, "b", null, false)
        assert aSet.containsAll (asList ("a", "b", false))
    }

    @Test void "A list can be built from a sequence of elements and 'nulls' are discarted" () {
        List<?> aList = listOf ("a", null, "b", null, false)
        assert false == get (aList, 2)
        assert aList == asList ("a", "b", false)
        assert aList != asList (false, "b", "a")
    }

    @Test void "A map can be built from a stream of elements and 'nulls' are discarted" () {
        def aMap = mapOf (
            Stream.of (
                entry ("a", "z"),
                null,
                entry ("b", null),
                null,
                entry (null, null),
                null,
                entry ("c", 1)
            )
        )

        assert aMap.size () == 4
        assert 1 == get (aMap, "c")
        assert null == get (aMap, "b")
    }

    @Test void "A map can be built from a sequence of elements and 'nulls' are discarted" () {
        //noinspection GroovyAssignabilityCheck It is required to pass `nulls` for this test
        def aMap = mapOf (
            entry ("a", "z"),
            null,
            entry ("b", null),
            null,
            entry (null, null),
            null,
            entry ("c", 1)
        )

        assert aMap.size () == 4
        assert 1 == get (aMap, "c")
        assert null == get (aMap, "b")
    }

    @Test void "A typed map can be created with 'typedMapOf'" () {
        //noinspection GroovyAssignabilityCheck It is required to pass `nulls` for this test
        def aMap = typedMapOf (
            entry ("a", 1),
            null,
            entry ("b", null),
            null,
            entry (null, null),
            null,
            entry ("c", 2)
        )

        assert aMap.size () == 4
        assert 1 == get (aMap, "a")
        assert null == get (aMap, "b")
        assert 2 == get (aMap, "c")
    }

    @Test void "Any set of 'null' elements results in an empty collection" () {
        assert mapOf (null, null).isEmpty ()
        assert mapOf ((Map.Entry<Object, Object>[])null).isEmpty ()
        assert listOf (null, null).isEmpty ()
        assert listOf ((Object[])null).isEmpty ()
        assert setOf (null, null).isEmpty ()
        assert setOf ((Object[])null).isEmpty ()
    }

    @Test void "Get methods return the searched node" () {
        Map<?, ?> person = createSampleMap ()

        assert get (person, "sports", 2, "active") == true
        assert get (person, "address", "street") == "C/Albufera"
    }

    @Test void "Get methods throws an error when the searched node does not exist" () {
        Map<Object, ?> person = createSampleMap ()

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
                assert !e.message.isEmpty ()
            }
        }
    }
}
