package com.willfp.eco.core.lookup;

import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Interface for testing if any object matches another object.
 *
 * @param <T> The type of object.
 */
public interface Testable<T> extends Predicate<T> {
    /**
     * If object matches the test.
     *
     * @param other The other object.
     * @return If matches.
     */
    boolean matches(@Nullable T other);

    @Override
    default boolean test(@Nullable T other) {
        return this.matches(other);
    }
}
