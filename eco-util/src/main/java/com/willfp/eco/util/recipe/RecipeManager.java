package com.willfp.eco.util.recipe;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class RecipeManager extends PluginDependent {
    /**
     * Registry of all recipes.
     */
    private final BiMap<String, EcoShapedRecipe> registry = HashBiMap.create();

    /**
     * Pass an {@link AbstractEcoPlugin} in order to interface with it.
     *
     * @param plugin The plugin to manage.
     */
    public RecipeManager(@NotNull final AbstractEcoPlugin plugin) {
        super(plugin);
    }

    void register(@NotNull final EcoShapedRecipe recipe) {
        String key = recipe.getKey();
        registry.forcePut(key, recipe);

        NamespacedKey baseKey = this.getPlugin().getNamespacedKeyFactory().create(key);
        Bukkit.getServer().removeRecipe(baseKey);

        NamespacedKey displayedKey = this.getPlugin().getNamespacedKeyFactory().create(key + "_displayed");
        Bukkit.getServer().removeRecipe(displayedKey);

        ShapedRecipe shapedRecipe = new ShapedRecipe(baseKey, recipe.getOutput());
        shapedRecipe.shape("012", "345", "678");
        for (int i = 0; i < 9; i++) {
            char character = String.valueOf(i).toCharArray()[0];
            shapedRecipe.setIngredient(character, recipe.getMaterialAtIndex(i));
        }

        ShapedRecipe displayedRecipe = new ShapedRecipe(displayedKey, recipe.getOutput());
        displayedRecipe.shape("012", "345", "678");
        for (int i = 0; i < 9; i++) {
            char character = String.valueOf(i).toCharArray()[0];
            displayedRecipe.setIngredient(character, new RecipeChoice.ExactChoice(recipe.getDisplayedAtIndex(i)));
        }

        Bukkit.getServer().addRecipe(displayedRecipe);
        Bukkit.getServer().addRecipe(shapedRecipe);
    }

    /**
     * Get recipe matching matrix.
     *
     * @param matrix The matrix to test.
     * @return The match, or null if not found.
     */
    @Nullable
    public EcoShapedRecipe getMatch(@NotNull final ItemStack[] matrix) {
        return registry.values().stream().filter(recipe -> recipe.test(matrix)).findFirst().orElse(null);
    }

    /**
     * Get shaped recipe by key.
     *
     * @param key The key.
     * @return The shaped recipe, or null if not found.
     */
    @Nullable
    public EcoShapedRecipe getShapedRecipe(@NotNull final String key) {
        return registry.get(key);
    }
}
