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
     * Register the recipe with eco and with the server.
     * <p>
     * Adds the recipe to {@link com.willfp.eco.core.recipe.Recipes} and schedules the
     * corresponding Bukkit recipes (the real recipe, the displayed recipe if displayed
     * recipes are enabled, and the Crafter recipe if {@link #isCrafterSupported()}).
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
     * Get the recipe key, namespaced under the ID of the owning plugin.
     *
     * @return The key.
     */
    @NotNull
    NamespacedKey getKey();

    /**
     * Get the displayed recipe key, which is {@link #getKey()} with a
     * {@code _displayed} suffix on the key portion.
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

    /**
     * Whether this recipe also fires inside the vanilla Crafter block.
     * <p>
     * When true, {@link #register()} additionally schedules a Bukkit recipe
     * (shaped or shapeless, matching the implementation) at the key
     * {@code <namespace>:<key>_crafter} with
     * {@link org.bukkit.inventory.RecipeChoice.ExactChoice} ingredients, so the
     * Crafter block can match and auto-craft this recipe; eco's
     * {@code AutocrafterPatch} skips its cancellation for these recipes.
     *
     * @return True if the recipe supports the Crafter block.
     */
    default boolean isCrafterSupported() {
        return false;
    }
}
