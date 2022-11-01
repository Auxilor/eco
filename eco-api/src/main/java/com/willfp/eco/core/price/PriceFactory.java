package com.willfp.eco.core.price;

import com.willfp.eco.core.math.MathContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

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
        return create(MathContext.EMPTY, (ctx) -> value);
    }

    /**
     * Create the price.
     *
     * @param baseContext The base MathContext.
     * @param function    The function to use. Should use {@link MathContext#copyWithPlayer(MathContext, Player)} on calls.
     * @return The price.
     */
    default @NotNull Price create(@NotNull final MathContext baseContext,
                                  @NotNull final Function<MathContext, Double> function) {
        return create(function.apply(baseContext));
    }
}
