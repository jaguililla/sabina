package co.there4.bali

import org.testng.annotations.Test

import static Throwables.*

@Test class ThrowablesTest {
    @Test (expectedExceptions = IllegalArgumentException)
    void "filter a 'null' exception throws an error" () {
        filter (null, "")
    }

    @Test (expectedExceptions = IllegalArgumentException)
    void "filter an exception with a 'null' prefix throws an error" () {
        filter (new RuntimeException(), null)
    }

    @Test void "filtering an exception with an empty string do not change the stack" () {
        Throwable t = new RuntimeException ()
        println (printThrowable (t))
        assert Arrays.equals (t.getStackTrace (), filter (t, "").getStackTrace ())
    }

    @Test void "filtering an exception with a package only returns frames of that package" () {
        Throwable t = new RuntimeException ()
        println (printThrowable (t))
        filter (t, "sabina").getStackTrace ().each {
            assert it.className.startsWith ("sabina")
        }
    }

    @Test (expectedExceptions = IllegalArgumentException)
    void "printing a 'null' exception will generate an error" () {
        printThrowable (null)
    }

    @Test void "printing an exception returns its stack trace in the string" () {
        String trace = printThrowable (new RuntimeException ())
        println (trace)
        assert trace.startsWith ("java.lang.RuntimeException")
        assert trace.contains ("\tat co.there4.bali.ThrowablesTest")
    }
}
