package com.willfp.eco.core.price.impl;

import com.willfp.eco.core.drops.DropQueue;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.price.Price;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Item-based price.
 */
public final class PriceItem implements Price {
    /**
     * The amount of items.
     */
    private final Supplier<Integer> amountToRemove;

    /**
     * The item.
     */
    private final TestableItem item;

    /**
     * Create a new economy-based price.
     *
     * @param amount The amount.
     * @param item   The item.
     */
    public PriceItem(final int amount,
                     @NotNull final TestableItem item) {
        this(() -> amount, item);
    }

    /**
     * Create a new economy-based price.
     *
     * @param amount The amount.
     * @param item   The item.
     */
    public PriceItem(@NotNull final Supplier<@NotNull Integer> amount,
                     @NotNull final TestableItem item) {
        this.amountToRemove = amount;
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
    public boolean canAfford(@NotNull final Player player) {
        int toRemove = amountToRemove.get();
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
    public void pay(@NotNull final Player player) {
        int toRemove = amountToRemove.get();
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
    public void giveTo(@NotNull final Player player) {
        new DropQueue(player)
                .addItem(item.getItem())
                .forceTelekinesis()
                .push();
    }
}
