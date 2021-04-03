package com.willfp.eco.util.recipe.recipes;

import com.willfp.eco.util.recipe.parts.RecipePart;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface CraftingRecipe {
    /**
     * Get item material at a specific index.
     *
     * @param index The index to check.
     * @return The material.
     */
    Material getMaterialAtIndex(int index);

    /**
     * Get "real" item at specific index.
     *
     * @param index The index to check.
     * @return The item.
     */
    ItemStack getDisplayedAtIndex(int index);

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
    RecipePart[] getParts();

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
