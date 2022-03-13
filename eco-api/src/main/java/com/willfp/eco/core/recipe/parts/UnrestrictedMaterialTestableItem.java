package com.willfp.eco.core.recipe.parts;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Same as material testable items, but doesn't filter out custom items.
 */
public class UnrestrictedMaterialTestableItem extends MaterialTestableItem {
    /**
     * Create a new simple recipe part.
     *
     * @param material The material.
     */
    public UnrestrictedMaterialTestableItem(@NotNull final Material material) {
        super(material);
    }

    /**
     * If the item matches the material.
     *
     * @param itemStack The item to test.
     * @return If the item is of the specified material.
     */
    @Override
    public boolean matches(@Nullable final ItemStack itemStack) {
        return itemStack != null && itemStack.getType() == this.getMaterial();
    }
}

