package com.willfp.eco.core.integrations.shop;

import com.willfp.eco.core.integrations.IntegrationRegistry;
import com.willfp.eco.core.price.Price;
import com.willfp.eco.core.price.impl.PriceFree;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to handle shop integrations.
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
     * Register eco item provider for shop plugins.
     */
    public static void registerEcoProvider() {
        REGISTRY.forEachSafely(ShopIntegration::registerEcoProvider);
    }

    /**
     * Get if an item is sellable for a player.
     *
     * @param itemStack The item.
     * @param player    The player.
     * @return If sellable.
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
     * @param itemStack The item.
     * @param player    The player.
     * @return The price.
     */
    @NotNull
    public static Price getUnitValue(@Nullable final ItemStack itemStack,
                                     @NotNull final Player player) {
        if (itemStack == null) {
            return new PriceFree();
        }

        return REGISTRY.firstSafely(new PriceFree(), integration -> integration.getUnitValue(itemStack, player));
    }

    /**
     * Get the price of an item.
     *
     * @param itemStack The item.
     * @return The price.
     * @deprecated Use getValue instead. This will always return 0 as prices depend on players.
     */
    @Deprecated(since = "6.47.0", forRemoval = true)
    public static double getItemPrice(@Nullable final ItemStack itemStack) {
        return getItemPrice(itemStack, null);
    }

    /**
     * Get the price of an item.
     *
     * @param itemStack The item.
     * @param player    The player.
     * @return The price.
     * @deprecated Use getValue instead. Null players / null items will always return 0.
     */
    @Deprecated(since = "6.47.0", forRemoval = true)
    public static double getItemPrice(@Nullable final ItemStack itemStack,
                                      @Nullable final Player player) {
        if (itemStack == null || player == null) {
            return 0.0;
        }

        return REGISTRY.firstSafely(
                0.0,
                integration -> integration.getUnitValue(itemStack, player).getValue(player, itemStack.getAmount())
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
