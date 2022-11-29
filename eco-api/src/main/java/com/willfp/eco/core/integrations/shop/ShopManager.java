package com.willfp.eco.core.integrations.shop;

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
public final class ShopManager {
    /**
     * A set of all registered integrations.
     */
    private static final Set<ShopIntegration> REGISTERED = new HashSet<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final ShopIntegration integration) {
        REGISTERED.removeIf(it -> it.getPluginName().equalsIgnoreCase(integration.getPluginName()));
        REGISTERED.add(integration);
    }

    /**
     * Register eco item provider for shop plugins.
     */
    public static void registerEcoProvider() {
        for (ShopIntegration shopIntegration : REGISTERED) {
            shopIntegration.registerEcoProvider();
        }
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

        for (ShopIntegration integration : REGISTERED) {
            return integration.isSellable(itemStack, player);
        }

        return false;
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

        for (ShopIntegration integration : REGISTERED) {
            return integration.getUnitValue(itemStack, player);
        }

        return new PriceFree();
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

        for (ShopIntegration shopIntegration : REGISTERED) {
            return shopIntegration.getUnitValue(itemStack, player).getValue(player, itemStack.getAmount());
        }

        return 0.0;
    }

    /**
     * Get all registered integrations.
     *
     * @return The integrations.
     */
    public static Set<ShopIntegration> getRegisteredIntegrations() {
        return new HashSet<>(REGISTERED);
    }

    private ShopManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
