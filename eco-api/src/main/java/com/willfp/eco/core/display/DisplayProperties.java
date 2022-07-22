package com.willfp.eco.core.display;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Extra properties passed into {@link DisplayModule}.
 *
 * @param inInventory  If the item was in an inventory.
 * @param inGui        If the item is assumed to be in a gui. (Not perfectly accurate).
 * @param originalItem The original item, not to be modified.
 */
public record DisplayProperties(
        boolean inInventory,
        boolean inGui,
        @NotNull ItemStack originalItem
) {
}
