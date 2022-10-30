package com.willfp.eco.core.price;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

/**
 * Create prices.
 * <p>
 * You must override one of the create methods.
 */
public interface PriceFactory {
    /**
     * Get the names (how the price looks in lookup strings).
     * <p>
     * For example, for XP Levels this would be 'l', 'xpl', 'levels', etc.
     *
     * @return The allowed names.
     */
    @NotNull List<String> getNames();

    /**
     * Create the price.
     *
     * @param value The value.
     * @return The price.
     */
    default @NotNull Price create(final double value) {
        return create(() -> value);
    }

    /**
     * Create the price.
     *
     * @param function The value function.
     * @return The price.
     */
    default @NotNull Price create(@NotNull final Supplier<@NotNull Double> function) {
        return create(function.get());
    }
}
