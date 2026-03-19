package com.willfp.eco.core.price;

import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import com.willfp.eco.core.placeholder.context.PlaceholderContextSupplier;
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
     * @param baseContext The base PlaceholderContext.
     * @param function    The function to use. Should use {@link PlaceholderContext#copyWithPlayer(org.bukkit.entity.Player)} on calls.
     * @return The price.
     */
    @NotNull Price create(@NotNull PlaceholderContext baseContext,
                          @NotNull PlaceholderContextSupplier<Double> function);
}
