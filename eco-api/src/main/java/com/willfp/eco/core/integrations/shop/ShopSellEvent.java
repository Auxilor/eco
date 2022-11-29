package com.willfp.eco.core.integrations.shop;

import com.willfp.eco.core.price.Price;
import com.willfp.eco.core.price.impl.PriceEconomy;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Unified event for shop plugins to fire in order to have sell multipliers.
 */
public class ShopSellEvent extends PlayerEvent {
    /**
     * The event handler list.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * The sell price.
     */
    private Price price;

    /**
     * The price multiplier.
     */
    private double multiplier;

    /**
     * The item to be sold.
     */
    @Nullable
    private final ItemStack item;

    /**
     * Create new shop sell event.
     *
     * @param who   The player.
     * @param price The price.
     * @param item  The item.
     * @deprecated Use the price system instead.
     */
    @Deprecated(since = "6.47.0", forRemoval = true)
    public ShopSellEvent(@NotNull final Player who,
                         final double price,
                         @Nullable final ItemStack item) {
        this(who, new PriceEconomy(price), item);
    }

    /**
     * Create new shop sell event.
     *
     * @param who   The player.
     * @param price The price.
     * @param item  The item.
     */
    public ShopSellEvent(@NotNull final Player who,
                         @NotNull final Price price,
                         @Nullable final ItemStack item) {
        this(who, price, item, 1.0);
    }

    /**
     * Create new shop sell event.
     *
     * @param who        The player.
     * @param price      The price.
     * @param item       The item.
     * @param multiplier The multiplier.
     */
    public ShopSellEvent(@NotNull final Player who,
                         @NotNull final Price price,
                         @Nullable final ItemStack item,
                         final double multiplier) {
        super(who);

        this.price = price;
        this.item = item;

        this.multiplier = multiplier;
    }

    /**
     * Get the value.
     *
     * @return The value.
     */
    @NotNull
    public Price getValue() {
        return this.price;
    }

    /**
     * Set the value.
     *
     * @param price The value.
     */
    public void setValue(@NotNull final Price price) {
        this.price = price;
    }

    /**
     * Get the item to be sold.
     *
     * @return The item. Can be null for some plugins, so check hasKnownItem first!
     */
    @Nullable
    public ItemStack getItem() {
        return item;
    }

    /**
     * Get if the item is known. Some shop plugins are lacking this in their event,
     * so always check this before getItem(), as getItem() may be null.
     *
     * @return If the item is known.
     */
    public boolean hasKnownItem() {
        return item != null;
    }

    /**
     * Get the price multiplier.
     *
     * @return The multiplier.
     */
    public double getMultiplier() {
        return multiplier;
    }

    /**
     * Set the price multiplier.
     *
     * @param multiplier The multiplier.
     */
    public void setMultiplier(final double multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * Get the price.
     *
     * @return The price.
     * @deprecated Use the price system instead.
     */
    @Deprecated(since = "6.47.0", forRemoval = true)
    public double getPrice() {
        return this.getValue().getValue(player);
    }

    /**
     * Set the price.
     *
     * @param price The price.
     * @deprecated Use the price system instead.
     */
    @Deprecated(since = "6.47.0", forRemoval = true)
    public void setPrice(final double price) {
        this.setValue(new PriceEconomy(price));
    }

    /**
     * Bukkit parity.
     *
     * @return The handlers.
     */
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * Bukkit parity.
     *
     * @return The handlers.
     */
    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
