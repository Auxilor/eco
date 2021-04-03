package com.willfp.eco.util.recipe.parts;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EmptyRecipePart implements RecipePart {
    /**
     * Create a new empty recipe part.
     */
    public EmptyRecipePart() {

    }

    /**
     * If the item is empty.
     *
     * @param itemStack The item to test.
     * @return If the item is empty.
     */
    @Override
    public boolean matches(@Nullable final ItemStack itemStack) {
        return itemStack == null || itemStack.getType() == Material.AIR;
    }

    @Override
    public ItemStack getDisplayed() {
        return new ItemStack(Material.AIR);
    }
}
