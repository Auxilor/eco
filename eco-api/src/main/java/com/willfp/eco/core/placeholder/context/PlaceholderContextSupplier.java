package com.willfp.eco.core.placeholder.context;

import org.jetbrains.annotations.NotNull;

/**
 * A supplier that takes a {@link PlaceholderContext} and returns a value.
 *
 * @param <T> The type of value to return.
 */
public interface PlaceholderContextSupplier<T> {
    /**
     * Get the value.
     *
     * @param context The context.
     * @return The value.
     */
    @NotNull
    T get(@NotNull PlaceholderContext context);
}
