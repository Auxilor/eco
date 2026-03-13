package com.willfp.eco.core.recipe.parts;

import com.google.common.base.Preconditions;
import com.willfp.eco.core.items.Items;
import com.willfp.eco.core.items.TestableItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Default vanilla items.
 */
public class MaterialTestableItem implements TestableItem {
    /**
     * The material.
     */
    private final Material material;

    /**
     * Create a new simple recipe part.
     *
     * @param material The material.
     */
    public MaterialTestableItem(@NotNull final Material material) {
        Preconditions.checkArgument(material != Material.AIR, "You can't have air as the type!");

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
        boolean simpleMatches = itemStack != null && itemStack.getType() == material;

        if (!simpleMatches) {
            return false;
        }

        return !Items.isCustomItem(itemStack);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(material);
    }

    /**
     * Get the material.
     *
     * @return The material.
     */
    public Material getMaterial() {
        return this.material;
    }
}
