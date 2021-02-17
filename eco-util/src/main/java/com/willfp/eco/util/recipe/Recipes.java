package com.willfp.eco.util.recipe;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.willfp.eco.util.recipe.recipes.EcoShapedRecipe;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
@SuppressWarnings("deprecation")
public class Recipes {
    /**
     * Registry of all recipes.
     */
    private static final BiMap<NamespacedKey, EcoShapedRecipe> RECIPES = HashBiMap.create();


    /**
     * Register a recipe.
     *
     * @param recipe The recipe.
     */
    public void register(@NotNull final EcoShapedRecipe recipe) {
        RECIPES.forcePut(recipe.getKey(), recipe);

        Bukkit.getServer().removeRecipe(recipe.getKey());
        Bukkit.getServer().removeRecipe(recipe.getDisplayedKey());

        ShapedRecipe shapedRecipe = new ShapedRecipe(recipe.getKey(), recipe.getOutput());
        shapedRecipe.shape("012", "345", "678");
        for (int i = 0; i < 9; i++) {
            char character = String.valueOf(i).toCharArray()[0];
            shapedRecipe.setIngredient(character, recipe.getMaterialAtIndex(i));
        }

        ShapedRecipe displayedRecipe = new ShapedRecipe(recipe.getDisplayedKey(), recipe.getOutput());
        displayedRecipe.shape("012", "345", "678");
        for (int i = 0; i < 9; i++) {
            char character = String.valueOf(i).toCharArray()[0];
            displayedRecipe.setIngredient(character, new RecipeChoice.ExactChoice(recipe.getDisplayedAtIndex(i)));
        }

        Bukkit.getServer().addRecipe(shapedRecipe);
        Bukkit.getServer().addRecipe(displayedRecipe);
    }

    /**
     * Get recipe matching matrix.
     *
     * @param matrix The matrix to test.
     * @return The match, or null if not found.
     */
    @Nullable
    public EcoShapedRecipe getMatch(@NotNull final ItemStack[] matrix) {
        return RECIPES.values().stream().filter(recipe -> recipe.test(matrix)).findFirst().orElse(null);
    }

    /**
     * Get shaped recipe by key.
     *
     * @param key The key.
     * @return The shaped recipe, or null if not found.
     */
    @Nullable
    public EcoShapedRecipe getShapedRecipe(@NotNull final NamespacedKey key) {
        EcoShapedRecipe recipe = RECIPES.get(key);
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
