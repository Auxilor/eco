package com.willfp.eco.core.price;

import com.willfp.eco.core.items.Items;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.price.impl.PriceEconomy;
import com.willfp.eco.core.price.impl.PriceFree;
import com.willfp.eco.core.price.impl.PriceItem;
import com.willfp.eco.core.price.impl.PriceWithDisplayText;
import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import com.willfp.eco.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
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
        String[] args = StringUtils.parseTokens(key.toLowerCase());

        if (args.length == 0) {
            return new PriceFree();
        }

        double value;

        try {
            value = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            value = 0.0;
        }

        if (args.length == 1) {
            return new PriceEconomy(value);
        }

        /*
        Janky 'arg parser' solution.
         */
        List<String> nameList = new ArrayList<>();
        String displayText = null;

        for (String arg : Arrays.copyOfRange(args, 1, args.length)) {
            if (arg.startsWith("display:")) {
                displayText = StringUtils.removePrefix(arg, "display:");
            } else {
                nameList.add(arg);
            }
        }

        String name = String.join(" ", nameList);

        Price price;

        PriceFactory factory = FACTORIES.get(name);

        if (factory == null) {
            TestableItem item = Items.lookup(name);

            if (item instanceof EmptyTestableItem) {
                return new PriceFree();
            }

            price = new PriceItem((int) Math.round(value), item);
        } else {
            price = factory.create(value);
        }

        if (displayText == null) {
            return price;
        } else {
            return new PriceWithDisplayText(price, displayText);
        }
    }

    private Prices() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
