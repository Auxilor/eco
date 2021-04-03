package com.willfp.eco.core.recipe.parts;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface RecipePart {
    /**
     * If an ItemStack matches the recipe part.
     *
     * @param itemStack The item to test.
     * @return If the item matches.
     */
    boolean matches(@Nullable ItemStack itemStack);

    /**
     * Get a displayed itemstack, for autocraft.
     *
     * @return The item, displayed.
     */
    ItemStack getDisplayed();
}

