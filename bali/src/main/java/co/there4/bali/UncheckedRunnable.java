package co.there4.bali;

/**
 * Runnable which allows blocks of code that throw checked exceptions.
 */
@FunctionalInterface public interface UncheckedRunnable extends Runnable {
    @Override default void run () {
        try {
            uncheckedRun ();
        }
        catch (Exception e) {
            throw Throwables.propagate (e);
        }
    }

    void uncheckedRun () throws Exception; // NOSONAR Define a dedicated exception
}
