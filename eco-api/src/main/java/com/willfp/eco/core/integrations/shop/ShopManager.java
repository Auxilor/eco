package com.willfp.eco.core.integrations.shop;

import com.willfp.eco.core.integrations.IntegrationRegistry;
import com.willfp.eco.core.price.Price;
import com.willfp.eco.core.price.impl.PriceFree;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class to handle shop integrations.
 * <p>
 * Sellability is checked against every registered {@link ShopIntegration}, but item values are
 * taken from a single one. If none is registered, nothing is sellable and every item is free.
 */
@SuppressWarnings("DeprecatedIsStillUsed")
public final class ShopManager {
    /**
     * A set of all registered integrations.
     */
    private static final IntegrationRegistry<ShopIntegration> REGISTRY = new IntegrationRegistry<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final ShopIntegration integration) {
        REGISTRY.register(integration);
    }

    /**
     * Register eco's item provider with every registered shop integration.
     */
    public static void registerEcoProvider() {
        REGISTRY.forEachSafely(ShopIntegration::registerEcoProvider);
    }

    /**
     * Get if an item is sellable for a player.
     *
     * @param itemStack The item, or null.
     * @param player    The player.
     * @return If sellable. False if the item is null.
     */
    public static boolean isSellable(@Nullable final ItemStack itemStack,
                                     @NotNull final Player player) {
        if (itemStack == null) {
            return false;
        }

        return REGISTRY.anySafely(integration -> integration.isSellable(itemStack, player));
    }

    /**
     * Get the value of one of an item for a player.
     * <p>
     * For example, if you pass in a stack, it will only return the value of <b>one</b> item, not the full stack.
     *
     * @param itemStack The item, or null.
     * @param player    The player.
     * @return The price. A {@link PriceFree} if the item is null or no integration is registered.
     */
    @NotNull
    public static Price getUnitValue(@Nullable final ItemStack itemStack,
                                     @NotNull final Player player) {
        if (itemStack == null) {
            return new PriceFree();
        }

        return REGISTRY.firstSafely(
                integration -> integration.getUnitValue(itemStack, player),
                new PriceFree()
        );
    }

    /**
     * Get the price of an item.
     *
     * @param itemStack The item, or null.
     * @return The price. Always 0, as prices depend on players.
     * @deprecated Use {@link #getUnitValue(ItemStack, Player)} instead. This will always
     *             return 0 as prices depend on players.
     */
    @Deprecated(since = "6.47.0", forRemoval = true)
    public static double getItemPrice(@Nullable final ItemStack itemStack) {
        return getItemPrice(itemStack, null);
    }

    /**
     * Get the price of an item.
     * <p>
     * Unlike {@link #getUnitValue(ItemStack, Player)}, this is the value of the whole stack,
     * not of a single item.
     *
     * @param itemStack The item, or null.
     * @param player    The player, or null.
     * @return The price. 0 if either argument is null.
     * @deprecated Use {@link #getUnitValue(ItemStack, Player)} instead. Null players / null
     *             items will always return 0.
     */
    @Deprecated(since = "6.47.0", forRemoval = true)
    public static double getItemPrice(@Nullable final ItemStack itemStack,
                                      @Nullable final Player player) {
        if (itemStack == null || player == null) {
            return 0.0;
        }

        return REGISTRY.firstSafely(
                integration -> integration.getUnitValue(itemStack, player).getValue(player, itemStack.getAmount()),
                0.0
        );
    }

    /**
     * Get all registered integrations.
     *
     * @return The integrations.
     */
    public static Set<ShopIntegration> getRegisteredIntegrations() {
        return new HashSet<>(REGISTRY.values());
    }

    private ShopManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
