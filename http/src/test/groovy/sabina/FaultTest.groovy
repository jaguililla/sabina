package sabina

import org.testng.annotations.Test

import java.util.function.BiConsumer

@Test public class FaultTest {
    @Test (expectedExceptions = IllegalArgumentException)
    public void "a fault handler can not be 'null'" () {
        new Fault<RuntimeException> (RuntimeException, null)
    }

    public void "a handler for an empty exception can be created" () {
        Fault<Exception> f = new Fault<> (null, {} as BiConsumer<Exception, Request>)
        assert f.handler != null
    }

    public void "a fault handler is printed showing the exception" () {
        Fault<RuntimeException> f =
            new Fault<> (RuntimeException, {} as BiConsumer<RuntimeException, Request>)

        assert f.exception.equals (RuntimeException)
        assert f.toString ().equals ("Fault: java.lang.RuntimeException")

        f = new Fault<> (null, {} as BiConsumer<RuntimeException, Request>)
        assert f.toString ().equals ("Fault: <empty>")
    }
}
