package com.willfp.eco.core.price;

import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import com.willfp.eco.core.placeholder.context.PlaceholderContextSupplier;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

/**
 * Create prices.
 * <p>
 * Override create(PlaceholderContext, PlaceholderContextSupplier), other methods
 * are for backwards compatibility.
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
     * @deprecated Use {@link #create(PlaceholderContext, PlaceholderContextSupplier)} instead.
     */
    @Deprecated(since = "6.56.0", forRemoval = true)
    default @NotNull Price create(final double value) {
        return create(PlaceholderContext.EMPTY, (ctx) -> value);
    }

    /**
     * Create the price.
     *
     * @param baseContext The base MathContext.
     * @param function    The function to use. Should use {@link com.willfp.eco.core.math.MathContext#copyWithPlayer(com.willfp.eco.core.math.MathContext, Player)} on calls.
     * @return The price.
     * @deprecated Use {@link #create(PlaceholderContext, PlaceholderContextSupplier)} instead.
     */
    @Deprecated(since = "6.56.0", forRemoval = true)
    @SuppressWarnings("removal")
    default @NotNull Price create(@NotNull final com.willfp.eco.core.math.MathContext baseContext,
                                  @NotNull final Function<com.willfp.eco.core.math.MathContext, Double> function) {
        return create(baseContext.toPlaceholderContext(), (PlaceholderContext ctx) -> function.apply(ctx.toMathContext()));
    }

    /**
     * Create the price.
     *
     * @param baseContext The base PlaceholderContext.
     * @param function    The function to use. Should use {@link PlaceholderContext#copyWithPlayer(Player)} on calls.
     * @return The price.
     */
    @SuppressWarnings("removal")
    default @NotNull Price create(@NotNull final PlaceholderContext baseContext,
                                  @NotNull final PlaceholderContextSupplier<Double> function) {
        return create(baseContext.toMathContext(), (com.willfp.eco.core.math.MathContext ctx) -> function.get(ctx.toPlaceholderContext()));
    }
}
