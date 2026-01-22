package com.willfp.eco.core.recipe;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.items.Items;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import com.willfp.eco.core.recipe.recipes.CraftingRecipe;
import com.willfp.eco.core.recipe.recipes.ShapedCraftingRecipe;
import com.willfp.eco.core.recipe.recipes.ShapelessCraftingRecipe;
import com.willfp.eco.util.NamespacedKeyUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Utility class to manage and register crafting recipes.
 */
public final class Recipes {

    /**
     * Registry of all recipes.
     */
    private static final BiMap<NamespacedKey, CraftingRecipe> RECIPES = HashBiMap.create();

    /**
     * Cached recipes from matrix.
     */
    private static final LoadingCache<ItemStack[], Optional<CraftingRecipe>> RECIPES_FROM_MATRIX = Caffeine.newBuilder()
            .maximumSize(2048L)
            .build(
                    matrix -> RECIPES.values().stream().filter(recipe -> recipe.test(matrix)).findFirst()
            );

    /**
     * Register a recipe.
     *
     * @param recipe The recipe.
     */
    public static void register(@NotNull final CraftingRecipe recipe) {
        RECIPES.forcePut(recipe.getKey(), recipe);
        RECIPES_FROM_MATRIX.invalidateAll();
    }

    /**
     * Get recipe matching matrix.
     *
     * @param matrix The matrix to test.
     * @return The match, or null if not found.
     */
    @Nullable
    public static CraftingRecipe getMatch(@Nullable final ItemStack[] matrix) {
        if (matrix == null) {
            return null;
        }

        return RECIPES_FROM_MATRIX.get(matrix).orElse(null);
    }

    /**
     * Get recipe by key.
     *
     * @param key The key.
     * @return The recipe, or null if not found.
     */
    @Nullable
    public static CraftingRecipe getRecipe(@NotNull final NamespacedKey key) {
        CraftingRecipe recipe = RECIPES.get(key);
        if (recipe != null) {
            return recipe;
        }

        if (key.getKey().contains("_displayed")) {
            NamespacedKey otherKey = NamespacedKeyUtils.create(
                    key.getNamespace(),
                    key.getKey().replace("_displayed", "")
            );

            return RECIPES.get(otherKey);
        }

        return null;
    }

    /**
     * Create and register recipe.
     *
     * @param plugin        The plugin.
     * @param key           The key.
     * @param output        The output.
     * @param recipeStrings The recipe.
     * @return The recipe.
     */
    public static CraftingRecipe createAndRegisterRecipe(@NotNull final EcoPlugin plugin,
                                                         @NotNull final String key,
                                                         @NotNull final ItemStack output,
                                                         @NotNull final List<String> recipeStrings) {
        return createAndRegisterRecipe(plugin, key, output, recipeStrings, null, false);
    }

    /**
     * Create and register recipe.
     *
     * @param plugin        The plugin.
     * @param key           The key.
     * @param output        The output.
     * @param recipeStrings The recipe strings (shaped or shapeless depending on flag).
     * @param permission    Optional permission required to craft.
     * @param shapeless     If true, treat as shapeless recipe (flat list). If false or omitted, treat as shaped (9 positions).
     * @return The recipe, or null if invalid.
     */
    @Nullable
    public static CraftingRecipe createAndRegisterRecipe(@NotNull final EcoPlugin plugin,
                                                         @NotNull final String key,
                                                         @NotNull final ItemStack output,
                                                         @NotNull final List<String> recipeStrings,
                                                         @Nullable final String permission,
                                                         final boolean shapeless) {

        NamespacedKey namespacedKey = plugin.getNamespacedKeyFactory().create(key);

        if (shapeless) {
            // Shapeless: flat list of ingredients
            ShapelessCraftingRecipe.Builder builder = ShapelessCraftingRecipe.builder(plugin, key)
                    .setOutput(output);

            if (permission != null) {
                builder.setPermission(permission);
            }

            boolean hasValid = false;

            for (String line : recipeStrings) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;

                TestableItem part = Items.lookup(trimmed);
                if (part != null && !(part instanceof EmptyTestableItem)) {
                    builder.addRecipePart(part);
                    hasValid = true;
                }
            }

            if (!hasValid) {
                plugin.getLogger().warning("Shapeless recipe " + plugin.getID() + ":" + key +
                        " has no valid ingredients — not registered.");
                return null;
            }

            ShapelessCraftingRecipe recipe = builder.build();
            recipe.register();
            return recipe;
        } else {
            // Shaped: exactly 9 positions
            if (recipeStrings.size() != 9) {
                plugin.getLogger().warning("Shaped recipe " + plugin.getID() + ":" + key +
                        " has " + recipeStrings.size() + " ingredients — expected exactly 9.");
                return null;
            }

            ShapedCraftingRecipe.Builder builder = ShapedCraftingRecipe.builder(plugin, key)
                    .setOutput(output);

            if (permission != null) {
                builder.setPermission(permission);
            }

            for (int i = 0; i < 9; i++) {
                builder.setRecipePart(i, Items.lookup(recipeStrings.get(i)));
            }

            if (builder.isAir()) {
                plugin.getLogger().warning("Shaped recipe " + plugin.getID() + ":" + key +
                        " consists only of air or invalid items — not registered.");
                return null;
            }

            ShapedCraftingRecipe recipe = builder.build();
            recipe.register();
            return recipe;
        }
    }

    private Recipes() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
