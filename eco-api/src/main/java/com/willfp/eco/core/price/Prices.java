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

import java.util.List;
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
     * {@code 200 g_souls}, {@code 200 pots of gold}, etc.
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
     * {@code 200 g_souls}, {@code 200 gold}, etc.
     *
     * @param key     The key.
     * @param context The context to do math in.
     * @return The price, or {@link PriceFree} if invalid.
     */
    @NotNull
    public static Price lookup(@NotNull final String key,
                               @NotNull final MathContext context) {
        List<String> args = List.of(key.split(" "));

        if (args.isEmpty()) {
            return new PriceFree();
        }

        // Step through arguments parsing the whole thing as an expression until it hits zero, and we've reached the point name.
        Double value = null;
        String priceName = null;

        for (int i = 0; i < args.size(); i++) {
            double valueUpTo = NumberUtils.evaluateExpression(
                    String.join(" ", args.subList(0, i)),
                    context
            );

            if (valueUpTo <= 0) {
                break;
            }

            value = valueUpTo;
            if (i == args.size() - 1) {
                priceName = args.get(args.size() - 1);
            }
        }

        // Value is null if there was no valid value
        if (value == null) {
            return new PriceFree();
        }

        // If price wasn't specified, default to economy
        if (priceName == null) {
            return new PriceEconomy(value);
        } else {
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
    }

    private Prices() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
