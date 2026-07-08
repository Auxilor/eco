package com.willfp.eco.core.price;

import com.willfp.eco.core.items.Items;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import com.willfp.eco.core.placeholder.context.PlaceholderContextSupplier;
import com.willfp.eco.core.price.impl.PriceEconomy;
import com.willfp.eco.core.price.impl.PriceFree;
import com.willfp.eco.core.price.impl.PriceItem;
import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import com.willfp.eco.util.NumberUtils;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class to manage prices.
 */
public final class Prices {
    /**
     * All factories.
     */
    private static final Map<String, PriceFactory> FACTORIES = new ConcurrentHashMap<>();

    /**
     * Returns an immutable snapshot of all loaded price factory names.
     *
     * @return Immutable set containing all loaded factory names
     */
    public static Set<String> allLoadedFactories() {
        return Set.copyOf(FACTORIES.keySet());
    }

    /**
     * Register a new price factory.
     *
     * @param factory The factory.
     */
    public static void registerPriceFactory(@NotNull final PriceFactory factory) {
        for (String name : factory.getNames()) {
            String key = name.toLowerCase();
            PriceFactory existing = FACTORIES.get(key);
            if (existing != null && existing != factory) {
                throw new IllegalStateException(String.format(
                        "A price factory is already registered under the name '%s' (%s). Cannot register %s.",
                        key, existing.getClass().getName(), factory.getClass().getName()
                ));
            }
            FACTORIES.put(key, factory);
        }
    }

    /**
     * Unregister a price factory by exact instance.
     * <p>
     * Only removes a name mapping if it currently points to this exact factory,
     * so it cannot evict a different factory that owns the same name.
     *
     * @param factory The factory to unregister.
     */
    public static void unregisterPriceFactory(@NotNull final PriceFactory factory) {
        for (String name : factory.getNames()) {
            FACTORIES.remove(name.toLowerCase(), factory);
        }
    }

    /**
     * Unregister whatever price factory is registered under a name.
     *
     * @param name The name to unregister.
     */
    public static void unregisterPriceFactory(@NotNull final String name) {
        FACTORIES.remove(name.toLowerCase());
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
        return create(expression, priceName, PlaceholderContext.EMPTY);
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
     * @deprecated Use {@link #create(String, String, PlaceholderContext)} instead.
     */
    @NotNull
    @Deprecated(since = "6.56.0", forRemoval = true)
    @SuppressWarnings("removal")
    public static Price create(@NotNull final String expression,
                               @Nullable final String priceName,
                               @NotNull final com.willfp.eco.core.math.MathContext context) {
        return create(expression, priceName, context.toPlaceholderContext());
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
                               @NotNull final PlaceholderContext context) {
        PlaceholderContextSupplier<Double> function = (ctx) -> NumberUtils.evaluateExpression(
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
