package com.willfp.eco.core.price;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Create prices.
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
    @NotNull Price create(double value);
}
