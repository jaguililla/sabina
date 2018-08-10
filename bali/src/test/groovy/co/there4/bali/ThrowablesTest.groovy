package co.there4.bali

import org.testng.annotations.Test

import static Throwables.*

@Test class ThrowablesTest {
    static class MyException extends IllegalArgumentException {}

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

    @Test(expectedExceptions = AssertionError)
    void 'Require exception fails if no exception is throw' () {
        requireException(IllegalArgumentException) {}
    }

    @Test(expectedExceptions = IllegalStateException)
    void 'Require exception works as expected' () {
        requireException(IllegalArgumentException) { throw new IllegalStateException() }
    }

    @Test void 'Require exception works with exception subtypes' () {
        requireException(IllegalArgumentException) { throw new MyException () {} }
    }

    @Test void 'Get cause of an exception returns the root exception' () {
        Throwable cause = new IllegalStateException()
        Throwable throwable = new RuntimeException(cause)
        assert getCause (throwable) == cause
    }
}
