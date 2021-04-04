package com.willfp.eco.core.recipe.recipes;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CraftingRecipe {
    /**
     * Test matrix against recipe.
     *
     * @param matrix The matrix to check.
     * @return If the recipe matches.
     */
    boolean test(@NotNull ItemStack[] matrix);

    /**
     * Register the recipe.
     */
    void register();

    /**
     * The recipe parts.
     *
     * @return The parts.
     */
    List<TestableItem> getParts();

    /**
     * Get the recipe key.
     *
     * @return The key.
     */
    NamespacedKey getKey();

    /**
     * Get the displayed recipe key.
     *
     * @return The key.
     */
    NamespacedKey getDisplayedKey();

    /**
     * Get the recipe output.
     *
     * @return The output.
     */
    ItemStack getOutput();
}
