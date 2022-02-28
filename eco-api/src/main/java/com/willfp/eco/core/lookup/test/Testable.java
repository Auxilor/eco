package com.willfp.eco.core.lookup.test;

import org.jetbrains.annotations.Nullable;

/**
 * Interface for testing if any object matches another object.
 *
 * @param <T> The type of object.
 */
public interface Testable<T> {
    /**
     * If object matches the test.
     *
     * @param other The other object.
     * @return If matches.
     */
    boolean matches(@Nullable T other);
}
