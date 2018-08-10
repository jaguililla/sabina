package co.there4.bali

import org.testng.annotations.Test

import java.util.function.Consumer
import java.util.function.Supplier

class UncheckedTest {

    private static void consumeSomething (Consumer<String> supplier) {
        supplier.accept ("")
    }

    private static void supplySomething (Supplier<String> supplier) {
        supplier.get ()
    }

    @Test(expectedExceptions = RuntimeException) void 'Exceptions are proccessed in consumer'() {
        consumeSomething ((UncheckedConsumer<String>) { throw new Exception() {} })
    }

    @Test(expectedExceptions = RuntimeException) void 'Exceptions are proccessed in supplier'() {
        supplySomething ((UncheckedSupplier<String>) { throw new Exception() {} })
    }

    @Test(expectedExceptions = RuntimeException) void 'Exceptions bypassed on supplier'() {
        Unchecked.get ({ throw new Exception() {} })
    }

    @Test(expectedExceptions = RuntimeException) void 'Exceptions bypassed on runnable'() {
        Unchecked.run ({
            throw new Exception() {}
        })
    }

    @Test void 'Finish properly without exceptions' () {
        consumeSomething ((UncheckedConsumer<String>) { assert it == ""})
        Unchecked.run ({ System.currentTimeMillis() })
        assert Unchecked.get ({"foo"}) == "foo"
    }
}
