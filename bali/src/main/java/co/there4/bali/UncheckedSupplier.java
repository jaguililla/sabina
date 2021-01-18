package co.there4.bali;

import java.util.function.Supplier;

/**
 * Supplier which allows blocks of code that throw checked exceptions.
 */
@FunctionalInterface public interface UncheckedSupplier<Z> extends Supplier<Z> {
    @Override default Z get () {
        try {
            return uncheckedGet ();
        }
        catch (Exception e) {
            throw Throwables.propagate (e);
        }
    }

    Z uncheckedGet () throws Exception; // NOSONAR Define a dedicated exception
}
