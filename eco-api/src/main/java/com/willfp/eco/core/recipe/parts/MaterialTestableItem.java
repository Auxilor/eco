package com.willfp.eco.core.recipe.parts;

import com.willfp.eco.core.items.TestableItem;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MaterialTestableItem implements TestableItem {
    /**
     * The material.
     */
    @Getter
    private final Material material;

    /**
     * Create a new simple recipe part.
     *
     * @param material The material.
     */
    public MaterialTestableItem(@NotNull final Material material) {
        this.material = material;
    }

    /**
     * If the item matches the material.
     *
     * @param itemStack The item to test.
     * @return If the item is of the specified material.
     */
    @Override
    public boolean matches(@Nullable final ItemStack itemStack) {
        return itemStack != null && itemStack.getType() == material;
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(material);
    }
}
