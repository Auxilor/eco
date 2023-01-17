package com.willfp.eco.core.price.impl;

import com.willfp.eco.core.drops.DropQueue;
import com.willfp.eco.core.items.HashedItem;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.math.MathContext;
import com.willfp.eco.core.price.Price;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Item-based price.
 */
public final class PriceItem implements Price {
    /**
     * The base MathContext.
     */
    private final MathContext baseContext;

    /**
     * The amount of items.
     */
    private final Function<MathContext, Double> function;

    /**
     * The item.
     */
    private final TestableItem item;

    /**
     * The multipliers.
     */
    private final Map<UUID, Double> multipliers = new HashMap<>();

    /**
     * Create a new item-based price.
     *
     * @param amount The amount.
     * @param item   The item.
     */
    public PriceItem(final int amount,
                     @NotNull final TestableItem item) {
        this(MathContext.EMPTY, ctx -> (double) amount, item);
    }

    /**
     * Create a new item-based price.
     *
     * @param baseContext The base MathContext.
     * @param function    The function to get the amount of items to remove.
     * @param item        The item.
     */
    public PriceItem(@NotNull final MathContext baseContext,
                     @NotNull final Function<MathContext, Double> function,
                     @NotNull final TestableItem item) {
        this.baseContext = baseContext;
        this.function = function;
        this.item = item;
    }

    /**
     * Get the item.
     *
     * @return The item.
     */
    public TestableItem getItem() {
        return item;
    }

    @Override
    public boolean canAfford(@NotNull final Player player,
                             final double multiplier) {
        int toRemove = (int) getValue(player, multiplier);
        if (toRemove <= 0) {
            return true;
        }

        int count = 0;

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (item.matches(itemStack)) {
                count += itemStack.getAmount();
            }
        }

        return count >= toRemove;
    }

    @Override
    public void pay(@NotNull final Player player,
                    final double multiplier) {
        int toRemove = (int) getValue(player, multiplier);
        int count = 0;

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (count >= toRemove) {
                break;
            }

            if (item.matches(itemStack)) {
                int itemAmount = itemStack.getAmount();

                if (itemAmount > toRemove) {
                    itemStack.setAmount(itemAmount - toRemove);
                }

                if (itemAmount <= toRemove) {
                    itemStack.setAmount(0);
                    itemStack.setType(Material.AIR);
                }

                count += itemAmount;
            }
        }
    }

    @Override
    public void giveTo(@NotNull final Player player,
                       final double multiplier) {
        ItemStack itemStack = item.getItem().clone();
        itemStack.setAmount((int) getValue(player, multiplier));

        new DropQueue(player)
                .addItem(itemStack)
                .forceTelekinesis()
                .push();
    }

    @Override
    public double getValue(@NotNull final Player player,
                           final double multiplier) {
        return Math.toIntExact(Math.round(
                this.function.apply(MathContext.copyWithPlayer(baseContext, player))
                        * getMultiplier(player) * multiplier
        ));
    }

    @Override
    public double getMultiplier(@NotNull final Player player) {
        return this.multipliers.getOrDefault(player.getUniqueId(), 1.0);
    }

    @Override
    public void setMultiplier(@NotNull final Player player,
                              final double multiplier) {
        this.multipliers.put(player.getUniqueId(), multiplier);
    }

    @Override
    public String getIdentifier() {
        return "eco:item-" + HashedItem.of(this.item.getItem()).getHash();
    }
}
