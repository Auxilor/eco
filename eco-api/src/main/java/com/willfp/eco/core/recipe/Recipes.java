package com.willfp.eco.core.recipe;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.items.Items;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import com.willfp.eco.core.recipe.recipes.CraftingRecipe;
import com.willfp.eco.core.recipe.recipes.ShapedCraftingRecipe;
import com.willfp.eco.core.recipe.recipes.ShapelessCraftingRecipe;
import com.willfp.eco.util.NamespacedKeyUtils;
import java.util.List;
import java.util.Optional;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * Variable representing timestamp at which last recipe was scheduled for registration.
     * Used to batch multiple registrations together.
     */
    private static long lastScheduledRegistration = 0L;

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

    /**
     * Create and register recipe.
     *
     * @param plugin        The plugin.
     * @param key           The key.
     * @param output        The output.
     * @param recipeStrings The recipe strings (shaped or shapeless depending on flag).
     * @param shapeless     If true, treat as shapeless recipe (flat list). If false or omitted, treat as shaped (9 positions).
     * @return The recipe, or null if invalid.
     */
    @Nullable
    public static CraftingRecipe createAndRegisterRecipe(@NotNull final EcoPlugin plugin,
                                                         @NotNull final String key,
                                                         @NotNull final ItemStack output,
                                                         @NotNull final List<String> recipeStrings,
                                                         final boolean shapeless) {
        return createAndRegisterRecipe(plugin, key, output, recipeStrings, null, shapeless);
    }

    /**
     * Create and register recipe.
     *
     * @param plugin        The plugin.
     * @param key           The key.
     * @param output        The output.
     * @param recipeStrings The recipe.
     * @param permission    Optional permission required to craft.
     * @return The recipe.
     */
    @Nullable
    public static CraftingRecipe createAndRegisterRecipe(@NotNull final EcoPlugin plugin,
                                                         @NotNull final String key,
                                                         @NotNull final ItemStack output,
                                                         @NotNull final List<String> recipeStrings,
                                                         @Nullable final String permission) {
        return createAndRegisterRecipe(plugin, key, output, recipeStrings, permission, false);
    }

    /**
     * Schedule a Bukkit recipe for registration, batching it with others if within a short time frame.
     * @param recipe the recipe
     */
    public static void scheduleBukkitRecipeRegistration(@NotNull final Recipe recipe) {
        Eco.get().addBukkitRecipeNoResend(recipe);
        lastScheduledRegistration = System.currentTimeMillis();
    }

    /**
     * Schedule a Bukkit recipe for removal, batching it with others if within a short time frame.
     * @param key the recipe key
     * @return true if the recipe was found and scheduled for removal, false if not found
     */
    public static boolean scheduleBukkitRecipeRemoval(@NotNull final NamespacedKey key) {
        var result = Eco.get().removeBukkitRecipeNoResend(key);
        if (result) {
            lastScheduledRegistration = System.currentTimeMillis();
        }
        return result;
    }

    /**
     * Force resend recipe updates to clients
     */
    public static void forceResendRecipeUpdates() {
        Eco.get().reloadBukkitRecipes();
    }

    /**
     * Check if it's been a while since the last recipe registration, and if so, force resend recipe updates to clients.
     */
    public static void checkBatching() {
        if (lastScheduledRegistration == 0L) {
            return;
        }

        if (System.currentTimeMillis() - lastScheduledRegistration > 3000L) {
            forceResendRecipeUpdates();
            Eco.get().getEcoPlugin().getLogger().info("Forced resend of recipe updates to clients after batching period.");
            lastScheduledRegistration = 0L;
        }
    }

    private Recipes() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
