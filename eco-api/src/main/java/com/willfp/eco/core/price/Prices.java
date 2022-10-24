package com.willfp.eco.core.price;

import com.willfp.eco.core.items.Items;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.math.MathContext;
import com.willfp.eco.core.price.impl.PriceEconomy;
import com.willfp.eco.core.price.impl.PriceFree;
import com.willfp.eco.core.price.impl.PriceItem;
import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import com.willfp.eco.util.NumberUtils;
import com.willfp.eco.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
     * Lookup a price from a string.
     * <p>
     * A price string should look like {@code 5000}, {@code 2000 levels},
     * {@code 200 * %level% souls}, {@code 200 crystals}, etc.
     * <p>
     * This does not support items as price names.
     *
     * @param key The key.
     * @return The price, or {@link PriceFree} if invalid.
     */
    @NotNull
    public static Price lookup(@NotNull final String key) {
        return lookup(key, MathContext.EMPTY);
    }

    /**
     * Lookup a price from a string.
     * <p>
     * A price string should look like {@code 5000}, {@code 2000 levels},
     * {@code 200 souls}, {@code 200 crystals}, etc.
     * <p>
     * This does not support items as price names.
     *
     * @param key     The key.
     * @param context The context to do math in.
     * @return The price, or {@link PriceFree} if invalid.
     */
    @NotNull
    public static Price lookup(@NotNull final String key,
                               @NotNull final MathContext context) {
        String[] args = StringUtils.parseTokens(key);

        if (args.length == 0) {
            return new PriceFree();
        }

        if (args.length == 1) {
            return create(args[0], null, context);
        }

        String exprWithName = String.join(" ", Arrays.copyOfRange(args, 0, args.length - 1));
        String priceName = args[args.length - 1];

        String exprWithoutName = String.join(" ", args);

        Price withName = create(exprWithName, priceName, context);
        Price withoutName = create(exprWithoutName, null, context);

        if (withoutName instanceof PriceFree) {
            return withName;
        } else {
            return withoutName;
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
        double value = NumberUtils.evaluateExpression(
                expression,
                context
        );

        if (value <= 0) {
            return new PriceFree();
        }

        // Default to economy
        if (priceName == null) {
            return new PriceEconomy(value);
        }

        // Find price factory
        PriceFactory factory = FACTORIES.get(priceName);

        // If no price factory, default to item price
        if (factory == null) {
            TestableItem item = Items.lookup(priceName);

            if (item instanceof EmptyTestableItem) {
                return new PriceFree();
            }

            return new PriceItem((int) Math.round(value), item);
        } else {
            return factory.create(value);
        }
    }

    private Prices() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
