package co.there4.bali;

/**
 * Unchecked exception utilities. To avoid the catch/throw pattern.
 */
public interface Unchecked {

    static void run (UncheckedRunnable lambda) {
        lambda.run ();
    }

    static <Z> Z get (UncheckedSupplier<Z> lambda) {
        return lambda.get ();
    }
}
