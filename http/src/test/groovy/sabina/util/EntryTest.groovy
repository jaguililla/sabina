package sabina.util

import org.testng.annotations.Test
import java.util.Map.Entry

import static sabina.util.Entry.*

/**
 * @author jam
 */
@Test public class EntryTest {
    @Test (expectedExceptions = UnsupportedOperationException.class)
    public void "change the value of an entry is not allowed" () {
        entry ("a", 1).setValue (2)
    }

    public void "two entries with same values are equal" () {
        Entry<String, Integer> e1 = entry ("a", 1)
        Entry<String, Integer> e2 = entry ("a", 1)

        assert e1.equals (e2) && e1.hashCode () == e2.hashCode ()
        e1 = e1.value 2
        assert !e1.equals (e2) && e1.hashCode () != e2.hashCode ()
        e2 = e2.value 2
        assert e1.equals (e2) && e1.hashCode () == e2.hashCode ()
    }

    public void "an instance is equal to itself" () {
        Entry<String, Integer> e = entry ("a", 1)
        assert e.equals (e)
    }

    @SuppressWarnings ("GrEqualsBetweenInconvertibleTypes")
    public void "comparing an entry with a different class always result in false" () {
        assert !entry ("a", 1).equals ("String")
    }

    public void "toString formats an entry properly" () {
        assert entry("a", 1).toString ().equals ("(a, 1)")
        assert entry(1, true).toString ().equals ("(1, true)")
        assert entry(null, true).toString ().equals ("(null, true)")
    }

    public void "if an entry changes its key or value results in a different instance" () {
        Entry<String, Integer> e = entry ("a", 1)
        assert e != e.key ("b")
        assert e.key ("b") != e.key ("b").value (2)
    }
}
