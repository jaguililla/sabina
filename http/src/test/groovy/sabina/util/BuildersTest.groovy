package sabina.util

import java.time.LocalDate
import java.util.function.Consumer

import static sabina.util.Builders.*

import org.testng.annotations.Test

import static sabina.util.Entry.entry

@Test class BuildersTest {
    @Test (expectedExceptions = IllegalStateException)
    public void "an instance of 'Builders' can not be created" () {
        _create ()
    }

    public void "a list with a supplier creates the correct instance" () {
        List<String> lst = list ({ new LinkedList<String> () }, {
            it.add "a"
            it.add "b"
        })
        assert lst instanceof LinkedList
        assert lst.size () == 2
        assert lst[0].equals ("a")
        assert lst[1].equals ("b")
    }

    public void "default list builder returns an ArrayList" () {
        List<String> lst = list ({
            it.add("1")
        } as Consumer<List<String>>)
        assert lst instanceof ArrayList
        assert lst.size () == 1
        assert lst[0].equals ("1")
    }

    public void "a list can be built from a sequence of elements" () {
        List<?> lst = list (
            "a",
            "b",
            false
        )
        assert false == get (lst, 2) // TODO Move to 'get' tests
        assert lst.equals (Arrays.asList ("a", "b", false))
    }

    public void "a list with a given supplier can be built from a sequence of elements" () {
        List<?> lst = list ({ new LinkedList<?> () },
            "a",
            "b",
            false
        )

        assert lst instanceof LinkedList
        assert false == get (lst, 2) // TODO Move to 'get' tests
        assert lst.equals (Arrays.asList ("a", "b", false))
    }

    public void "usage examples" () {
        Map<?, ?> m = map (
            mapEntry ("m1", new LinkedHashMap<> ()),
            listEntry ("dt", new ArrayList<> ()),
            mapEntry ("m",
                listEntry ("m1", true, false)
            ),
            listEntry ("lst", {
                it.add (0)
                it.add ("")
            } as Consumer<List<?>>),
            entry ("", list ("", ""))
        )

        assert 0 == get (m, "lst", 0)
        assert 0 == ((List<?>)m.get ("lst")).get(0)
        assert true == ((List<?>)((Map<?, ?>)m.get ("m")).get ("m1")).get(0)

        Map<?, ?> person = map (
            entry ("name", "Juanjo"),
            entry ("surname", "Aguililla"),
            entry ("birth", LocalDate.of (1979, 1, 22)),
            mapEntry ("address",
                entry ("street", "C/Albufera")
            ),
            listEntry ("sports",
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
            )
        )

        assert get (person, "sports", 2, "active") == true
        assert get (person, "address", "street").equals ("C/Albufera")
    }
}
