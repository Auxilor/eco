package com.willfp.eco.core.lookup;

import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for testing if any object matches another object.
 * <p>
 * Extends {@link Predicate}, where {@link #test(Object)} delegates to
 * {@link #matches(Object)}.
 *
 * @param <T> The type of object.
 */
public interface Testable<T> extends Predicate<T> {
    /**
     * If object matches the test.
     *
     * @param other The other object, which may be null.
     * @return If matches.
     */
    boolean matches(@Nullable T other);

    @Override
    default boolean test(@Nullable T other) {
        return this.matches(other);
    }
}
