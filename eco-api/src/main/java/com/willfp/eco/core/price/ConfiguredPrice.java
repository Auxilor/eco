package com.willfp.eco.core.price;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import com.willfp.eco.core.price.impl.PriceFree;
import com.willfp.eco.core.serialization.ConfigDeserializer;
import com.willfp.eco.util.NumberUtils;
import com.willfp.eco.util.StringUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A price that can be shown to a player.
 */
public final class ConfiguredPrice implements Price {
    /**
     * The deserializer.
     */
    private static final ConfigDeserializer<ConfiguredPrice> DESERIALIZER = new Deserializer();

    /**
     * Free.
     */
    public static final ConfiguredPrice FREE = new ConfiguredPrice(
            new PriceFree(),
            "Free"
    );

    /**
     * The price.
     */
    private final Price price;

    /**
     * The format string.
     */
    private final String formatString;

    /**
     * Create a new Configured Price.
     *
     * @param price        The price.
     * @param formatString The format string.
     */
    public ConfiguredPrice(@NotNull final Price price,
                           @NotNull final String formatString) {
        this.price = price;
        this.formatString = formatString;
    }

    @Override
    public boolean canAfford(@NotNull final Player player,
                             final double multiplier) {
        return this.price.canAfford(player, multiplier);
    }

    @Override
    public void pay(@NotNull final Player player,
                    final double multiplier) {
        this.price.pay(player, multiplier);
    }

    @Override
    public void giveTo(@NotNull final Player player,
                       final double multiplier) {
        this.price.giveTo(player, multiplier);
    }

    @Override
    public double getValue(@NotNull final Player player,
                           final double multiplier) {
        return this.price.getValue(player, multiplier);
    }

    @Override
    public double getMultiplier(@NotNull final Player player) {
        return this.price.getMultiplier(player);
    }

    @Override
    public void setMultiplier(@NotNull final Player player,
                              final double multiplier) {
        this.price.setMultiplier(player, multiplier);
    }

    @Override
    public String getIdentifier() {
        return this.price.getIdentifier();
    }

    /**
     * Get the price that this delegates to.
     *
     * @return The price.
     */
    public Price getPrice() {
        return price;
    }

    /**
     * Get the display string for a player.
     *
     * @param player The player.
     * @return The display string.
     */
    public String getDisplay(@NotNull final Player player) {
        return this.getDisplay(player, 1.0);
    }

    /**
     * Get the display string for a player.
     *
     * @param player     The player.
     * @param multiplier The multiplier.
     * @return The display string.
     */
    public String getDisplay(@NotNull final Player player,
                             final double multiplier) {
        return StringUtils.format(
                formatString.replace("%value%", NumberUtils.format(this.getPrice().getValue(player, multiplier))),
                player,
                StringUtils.FormatOption.WITH_PLACEHOLDERS
        );
    }

    /**
     * Parse a configured price from config.
     *
     * @param config The config.
     * @return The price, or null if it's invalid.
     */
    @Nullable
    public static ConfiguredPrice create(@NotNull final Config config) {
        return DESERIALIZER.deserialize(config);
    }

    /**
     * Parse a configured price from config.
     *
     * @param config The config.
     * @return The price, or free if invalid.
     */
    @NotNull
    public static ConfiguredPrice createOrFree(@NotNull final Config config) {
        return Objects.requireNonNullElse(create(config), FREE);
    }

    /**
     * The deserializer for {@link ConfiguredPrice}.
     */
    private static final class Deserializer implements ConfigDeserializer<ConfiguredPrice> {
        @Override
        @Nullable
        public ConfiguredPrice deserialize(@NotNull final Config config) {
            if (!(
                    config.has("value")
                            && config.has("type")
            )) {
                return null;
            }

            String formatString;

            String langConfig = Eco.get().getEcoPlugin().getLangYml()
                    .getSubsections("price-display")
                    .stream()
                    .filter(section -> section.getString("type").equalsIgnoreCase(config.getString("type")))
                    .findFirst()
                    .map(section -> section.getString("display"))
                    .orElse(null);

            if (langConfig != null) {
                formatString = langConfig;
            } else if (config.has("display")) {
                formatString = config.getString("display");
            } else {
                return null;
            }

            Price price = Prices.create(
                    config.getString("value"),
                    config.getString("type"),
                    PlaceholderContext.of(config)
            );

            return new ConfiguredPrice(price, formatString);
        }
    }
}
