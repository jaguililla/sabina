package co.there4.bali;

import java.util.function.Consumer;

/**
 * Consumer which allows blocks of code that throw checked exceptions.
 */
@FunctionalInterface public interface UncheckedConsumer<Z> extends Consumer<Z> {
    @Override default void accept (Z object) {
        try {
            uncheckedAccept (object);
        }
        catch (Exception e) {
            throw Throwables.propagate (e);
        }
    }

    void uncheckedAccept (Z object) throws Exception; // NOSONAR Define a dedicated exception
}
