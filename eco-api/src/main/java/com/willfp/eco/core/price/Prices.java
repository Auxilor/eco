package com.willfp.eco.core.price;

import com.willfp.eco.core.items.Items;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.math.MathContext;
import com.willfp.eco.core.price.impl.PriceEconomy;
import com.willfp.eco.core.price.impl.PriceFree;
import com.willfp.eco.core.price.impl.PriceItem;
import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import com.willfp.eco.util.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Class to manage prices.
 */
public final class Prices {
    /**
     * All factories.
     */
    private static final Map<String, PriceFactory> FACTORIES = new ConcurrentHashMap<>();

    /**
     * Register a new price factory.
     *
     * @param factory The factory.
     */
    public static void registerPriceFactory(@NotNull final PriceFactory factory) {
        for (String name : factory.getNames()) {
            FACTORIES.put(name.toLowerCase(), factory);
        }
    }

    /**
     * Create price from an expression (representing the value),
     * and a price name.
     * <p>
     * Supports items as price names.
     *
     * @param expression The expression for the value.
     * @param priceName  The price name.
     * @return The price, or free if invalid.
     */
    @NotNull
    public static Price create(@NotNull final String expression,
                               @Nullable final String priceName) {
        return create(expression, priceName, MathContext.EMPTY);
    }

    /**
     * Create price from an expression (representing the value),
     * and a price name. Uses a context to parse the expression.
     * <p>
     * Supports items as price names.
     *
     * @param expression The expression for the value.
     * @param priceName  The price name.
     * @param context    The math context to parse the expression.
     * @return The price, or free if invalid.
     */
    @NotNull
    public static Price create(@NotNull final String expression,
                               @Nullable final String priceName,
                               @NotNull final MathContext context) {
        Function<MathContext, Double> function = (ctx) -> NumberUtils.evaluateExpression(
                expression,
                ctx
        );

        // Default to economy
        if (priceName == null) {
            return new PriceEconomy(context, function);
        }

        // Find price factory
        PriceFactory factory = FACTORIES.get(priceName);

        // If no price factory, default to item price
        if (factory == null) {
            TestableItem item = Items.lookup(priceName);

            if (item instanceof EmptyTestableItem) {
                return new PriceFree();
            }

            return new PriceItem(context, function, item);
        } else {
            return factory.create(context, function);
        }
    }

    private Prices() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
