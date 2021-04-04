package com.willfp.eco.core.recipe;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.willfp.eco.core.recipe.recipes.CraftingRecipe;
import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
@SuppressWarnings("deprecation")
public class Recipes {
    /**
     * Registry of all recipes.
     */
    private static final BiMap<NamespacedKey, CraftingRecipe> RECIPES = HashBiMap.create();


    /**
     * Register a recipe.
     *
     * @param recipe The recipe.
     */
    public void register(@NotNull final CraftingRecipe recipe) {
        RECIPES.forcePut(recipe.getKey(), recipe);
    }

    /**
     * Get recipe matching matrix.
     *
     * @param matrix The matrix to test.
     * @return The match, or null if not found.
     */
    @Nullable
    public CraftingRecipe getMatch(@NotNull final ItemStack[] matrix) {
        return RECIPES.values().stream().filter(recipe -> recipe.test(matrix)).findFirst().orElse(null);
    }

    /**
     * Get recipe by key.
     *
     * @param key The key.
     * @return The recipe, or null if not found.
     */
    @Nullable
    public CraftingRecipe getRecipe(@NotNull final NamespacedKey key) {
        CraftingRecipe recipe = RECIPES.get(key);
        if (recipe != null) {
            return recipe;
        }

        if (key.getKey().contains("_displayed")) {
            NamespacedKey otherKey = new NamespacedKey(key.getNamespace(), key.getKey().replace("_displayed", ""));

            return RECIPES.get(otherKey);
        }

        return null;
    }
}
