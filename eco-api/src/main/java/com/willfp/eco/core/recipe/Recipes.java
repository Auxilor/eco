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
import java.util.Map;
import java.util.Optional;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.CraftItemEvent;
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

            if (permission != null && !permission.isBlank()) {
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
                        " has no valid ingredients - not registered.");
                return null;
            }

            ShapelessCraftingRecipe recipe = builder.build();
            recipe.register();
            return recipe;
        } else {
            // Shaped: exactly 9 positions
            if (recipeStrings.size() != 9) {
                plugin.getLogger().warning("Shaped recipe " + plugin.getID() + ":" + key +
                        " has " + recipeStrings.size() + " ingredients - expected exactly 9.");
                return null;
            }

            ShapedCraftingRecipe.Builder builder = ShapedCraftingRecipe.builder(plugin, key)
                    .setOutput(output);

            if (permission != null && !permission.isBlank()) {
                builder.setPermission(permission);
            }

            for (int i = 0; i < 9; i++) {
                builder.setRecipePart(i, Items.lookup(recipeStrings.get(i)));
            }

            if (builder.isAir()) {
                plugin.getLogger().warning("Shaped recipe " + plugin.getID() + ":" + key +
                        " consists only of air or invalid items - not registered.");
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

    /**
     * Cancel a {@link CraftItemEvent} that matched a vanilla recipe and
     * manually deliver a custom output instead. Use when an eco recipe
     * shares its shape/material with a vanilla recipe and Bukkit picked the
     * vanilla one (e.g. a custom iron sword colliding with the vanilla iron
     * pickaxe shape): the event fires with {@code event.getRecipe()} pointing
     * at the vanilla recipe, so vanilla would deliver the wrong item.
     *
     * <p>This method:
     * <ol>
     *   <li>Cancels the event so vanilla does not deliver or consume.</li>
     *   <li>Decrements every non-empty crafting matrix slot by one.</li>
     *   <li>Places {@code item} on the player's cursor (or stacks onto the
     *       existing cursor item where possible), shift-click delivers via
     *       {@code player.getInventory().addItem(item)} with any overflow
     *       dropped at the player's feet.</li>
     * </ol>
     *
     * <p>Single-iteration: even for a shift-click this only consumes one grid
     * worth of ingredients and delivers the {@code item} stack as supplied.
     *
     * @param event The CraftItemEvent to take over.
     * @param item  The item stack to deliver to the player.
     */
    public static void takeOverCraftItem(@NotNull final CraftItemEvent event,
                                         @NotNull final ItemStack item) {
        event.setCancelled(true);

        ItemStack[] matrix = event.getInventory().getMatrix();
        for (int i = 0; i < matrix.length; i++) {
            ItemStack stack = matrix[i];
            if (stack == null || stack.getType().isAir()) {
                continue;
            }
            if (stack.getAmount() <= 1) {
                matrix[i] = null;
            } else {
                stack.setAmount(stack.getAmount() - 1);
            }
        }
        event.getInventory().setMatrix(matrix);

        HumanEntity player = event.getWhoClicked();

        if (event.isShiftClick()) {
            Map<Integer, ItemStack> overflow = player.getInventory().addItem(item);
            overflow.values().forEach(drop ->
                    player.getWorld().dropItemNaturally(player.getLocation(), drop));
            return;
        }

        ItemStack cursor = event.getCursor();
        if (cursor == null || cursor.getType().isAir()) {
            player.setItemOnCursor(item);
        } else if (cursor.isSimilar(item) && cursor.getAmount() + item.getAmount() <= item.getMaxStackSize()) {
            cursor.setAmount(cursor.getAmount() + item.getAmount());
            player.setItemOnCursor(cursor);
        } else {
            Map<Integer, ItemStack> overflow = player.getInventory().addItem(item);
            overflow.values().forEach(drop ->
                    player.getWorld().dropItemNaturally(player.getLocation(), drop));
        }
    }

    private Recipes() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
