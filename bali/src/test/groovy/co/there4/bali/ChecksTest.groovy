package co.there4.bali

import org.testng.annotations.Test

@Test class ChecksTest implements Checks {
    @Test (expectedExceptions = IllegalArgumentException.class)
    void "a non compliant argument throws an exception" () {
        require (false)
    }

    @Test void "a compliant argument do not throw an exception" () {
        require (true)
    }

    @Test void "a non compliant argument throws an exception with the proper message" () {
        try {
            require (false, String.format("Bad argument: %s", "arg1"))
            assert false
        }
        catch (IllegalArgumentException e) {
            assert e.message == "Bad argument: arg1"
        }
    }

    @Test void 'Check arguments behaves as expected' () {
        requireNotEmpty 'not empty', 'argument'
        require true

        assertException (IllegalArgumentException) { require false }
        assertException (IllegalArgumentException) { requireNotEmpty null, 'argument' }
        assertException (IllegalArgumentException) { requireNotEmpty '', 'argument' }
    }

    @Test void 'Check states behaves as expected' () {
        checkNotEmpty 'not empty', 'state'
        checkEmpty '', 'state'
        checkEmpty null, 'state'
        check true

        assertException (IllegalStateException) { check false }
        assertException (IllegalStateException) { checkNotEmpty null, 'state' }
        assertException (IllegalStateException) { checkNotEmpty '', 'state' }

        assertException (IllegalStateException) { checkEmpty 'filled', 'state' }
    }

    private static <T extends Exception> void assertException (Class<T> exception, Runnable block) {
        try {
            block.run()
            assert false
        }
        catch(T e) {
            assert e.class == exception
        }
    }
}
