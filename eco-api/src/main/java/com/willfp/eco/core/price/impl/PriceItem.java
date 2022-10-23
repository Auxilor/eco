package com.willfp.eco.core.price.impl;

import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.price.Price;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Item-based price.
 */
public final class PriceItem implements Price {
    /**
     * The amount of items.
     */
    private final int amountToRemove;

    /**
     * The item.
     */
    private final TestableItem item;

    /**
     * Create a new economy-based price.
     *
     * @param amount The amount.
     */
    public PriceItem(final int amount,
                     @NotNull final TestableItem item) {
        this.amountToRemove = Math.max(0, amount);
        this.item = item;
    }

    @Override
    public boolean canAfford(@NotNull Player player) {
        if (amountToRemove == 0) {
            return true;
        }

        int count = 0;

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (item.matches(itemStack)) {
                count += itemStack.getAmount();
            }
        }

        return count >= amountToRemove;
    }

    @Override
    public void pay(@NotNull Player player) {
        int count = 0;

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (count >= amountToRemove) {
                break;
            }

            if (item.matches(itemStack)) {
                int itemAmount = itemStack.getAmount();

                if (itemAmount > amountToRemove) {
                    itemStack.setAmount(itemAmount - amountToRemove);
                }

                if (itemAmount <= amountToRemove) {
                    itemStack.setAmount(0);
                    itemStack.setType(Material.AIR);
                }

                count += itemAmount;
            }
        }
    }
}
