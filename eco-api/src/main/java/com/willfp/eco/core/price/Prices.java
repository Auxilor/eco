package com.willfp.eco.core.price;

import com.willfp.eco.core.items.Items;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.price.impl.PriceEconomy;
import com.willfp.eco.core.price.impl.PriceFree;
import com.willfp.eco.core.price.impl.PriceItem;
import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import org.jetbrains.annotations.NotNull;

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
     * {@code 200 g_souls}, {@code 200 pots of gold}, etc.
     *
     * @param key The key.
     * @return The price, or {@link PriceFree} if invalid.
     */
    @NotNull
    public static Price lookup(@NotNull final String key) {
        String[] split = key.split(" ");

        if (split.length == 0) {
            return new PriceFree();
        }

        double value;

        try {
            value = Double.parseDouble(split[0]);
        } catch (NumberFormatException e) {
            value = 0.0;
        }

        if (split.length == 1) {
            return new PriceEconomy(value);
        }

        String name = String.join(" ", Arrays.copyOfRange(split, 1, split.length));

        PriceFactory factory = FACTORIES.get(name.toLowerCase());

        if (factory == null) {
            TestableItem item = Items.lookup(name.toLowerCase());

            if (item instanceof EmptyTestableItem) {
                return new PriceFree();
            }

            return new PriceItem((int) Math.round(value), item);
        }

        return factory.create(value);
    }

    private Prices() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
