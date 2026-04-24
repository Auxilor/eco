package com.willfp.eco.core.recipe.recipes;

import com.willfp.eco.core.items.TestableItem;
import java.util.List;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Any crafting recipe.
 * <p>
 * A crafting recipe consists of {@link TestableItem}s.
 */
public interface CraftingRecipe {
    /**
     * Test matrix against recipe.
     *
     * @param matrix The matrix to check.
     * @return If the recipe matches.
     */
    boolean test(@Nullable ItemStack[] matrix);

    /**
     * Register the recipe.
     */
    void register();

    /**
     * The recipe parts.
     *
     * @return The parts.
     */
    @NotNull
    List<TestableItem> getParts();

    /**
     * Get the recipe key.
     *
     * @return The key.
     */
    @NotNull
    NamespacedKey getKey();

    /**
     * Get the displayed recipe key.
     *
     * @return The key.
     */
    @NotNull
    NamespacedKey getDisplayedKey();

    /**
     * Get the recipe output.
     *
     * @return The output.
     */
    @NotNull
    ItemStack getOutput();

    /**
     * Get the recipe permission.
     *
     * @return The permission.
     */
    @Nullable
    default String getPermission() {
        return null;
    }
}
