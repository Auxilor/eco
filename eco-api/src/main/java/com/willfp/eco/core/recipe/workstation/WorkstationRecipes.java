package com.willfp.eco.core.recipe.workstation;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.willfp.eco.core.recipe.Recipes;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Registry and utility class for all {@link WorkstationRecipe}s.
 * <p>
 * Recipes are stored by {@link NamespacedKey}. Bukkit-backed recipes (e.g.
 * {@link SmeltingRecipe}, {@link SmithingRecipe}) additionally track their
 * Bukkit keys so they can be removed cleanly on {@link #clear()}.
 */
public final class WorkstationRecipes {
    /**
     * All registered workstation recipes, keyed by their {@link NamespacedKey}.
     */
    private static final BiMap<NamespacedKey, WorkstationRecipe> recipes = HashBiMap.create();

    /**
     * Bukkit recipe keys registered by workstation recipes that delegate to the
     * Bukkit recipe system. Tracked so they can be removed on {@link #clear()}.
     */
    private static final Set<NamespacedKey> trackedBukkitKeys = new HashSet<>();

    /**
     * Recipes pending confirmation for a specific player, keyed by player UUID.
     * Used by packet-level handlers (e.g. brewing, grindstone) that must
     * associate a detected inventory interaction with a recipe before it completes.
     */
    private static final Map<UUID, WorkstationRecipe> pendingRecipes = new HashMap<>();

    /**
     * Optional hook invoked when a pending brew at a given location must be
     * cancelled. Set via {@link #registerBrewCancelHook(Consumer)}.
     */
    @Nullable private static Consumer<Location> brewCancelHook = null;

    /**
     * Callback fired when a custom brew completes (ingredient consumed, result set).
     * Allows plugins to handle ghost mode and effects for packet-intercepted brews.
     */
    @FunctionalInterface
    public interface BrewCompletedCallback {
        /**
         * Called when a custom brew completes.
         *
         * @param location     The brewing stand location.
         * @param recipe       The recipe that completed.
         * @param matchedSlots The base slots (0-2) that received the result.
         */
        void onBrewCompleted(@NotNull final Location location, @NotNull final BrewingRecipe recipe, @NotNull final List<Integer> matchedSlots);
    }

    @Nullable private static BrewCompletedCallback brewCompletedCallback = null;

    private WorkstationRecipes() {}

    /**
     * Register a workstation recipe.
     *
     * @param recipe The recipe to register.
     */
    public static void register(@NotNull final WorkstationRecipe recipe) {
        recipes.put(recipe.getKey(), recipe);
    }

    /**
     * Track a Bukkit recipe key so it is removed when {@link #clear()} is called.
     *
     * @param key The Bukkit recipe key to track.
     */
    public static void trackBukkitKey(@NotNull final NamespacedKey key) {
        trackedBukkitKeys.add(key);
    }

    /**
     * Get a recipe by its key.
     *
     * @param key The key.
     * @return The recipe, or null if not found.
     */
    @Nullable
    public static WorkstationRecipe getByKey(@NotNull final NamespacedKey key) {
        return recipes.get(key);
    }

    /**
     * Get all registered workstation recipes.
     *
     * @return Unmodifiable collection of all recipes.
     */
    @NotNull
    public static Collection<WorkstationRecipe> getAll() {
        return Collections.unmodifiableCollection(recipes.values());
    }

    /**
     * Get all registered recipes of a specific type.
     *
     * @param type The recipe class to filter by.
     * @param <T>  The recipe type.
     * @return Unmodifiable collection of matching recipes.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T extends WorkstationRecipe> Collection<T> getAll(@NotNull final Class<T> type) {
        return recipes.values().stream()
                .filter(type::isInstance)
                .map(r -> (T) r)
                .collect(Collectors.toList());
    }

    /**
     * Store a pending recipe for a player.
     * <p>
     * Used by packet handlers to associate an in-progress workstation interaction
     * with a recipe before the result is confirmed.
     *
     * @param playerId The player's UUID.
     * @param recipe   The recipe to store as pending.
     */
    public static void setPendingRecipe(@NotNull final UUID playerId, @NotNull final WorkstationRecipe recipe) {
        pendingRecipes.put(playerId, recipe);
    }

    /**
     * Get the pending recipe for a player.
     *
     * @param playerId The player's UUID.
     * @return The pending recipe, or null if none is stored.
     */
    @Nullable
    public static WorkstationRecipe getPendingRecipe(@NotNull final UUID playerId) {
        return pendingRecipes.get(playerId);
    }

    /**
     * Clear the pending recipe for a player.
     *
     * @param playerId The player's UUID.
     */
    public static void clearPendingRecipe(@NotNull final UUID playerId) {
        pendingRecipes.remove(playerId);
    }

    /**
     * Register a hook to cancel pending brews at a given location.
     * <p>
     * Called by the internal packet handler to wire up brewing-stand cancellation
     * logic when a custom brewing recipe is interrupted.
     *
     * @param hook Consumer that accepts the brewing-stand {@link Location} to cancel.
     */
    public static void registerBrewCancelHook(@NotNull final Consumer<Location> hook) {
        brewCancelHook = hook;
    }

    /**
     * Cancel any pending brew at the given location using the registered hook.
     * <p>
     * Does nothing if no hook has been registered.
     *
     * @param location The location of the brewing stand to cancel.
     */
    public static void cancelPendingBrew(@NotNull final Location location) {
        if (brewCancelHook != null) brewCancelHook.accept(location);
    }

    /**
     * Register a hook called when a custom brew completes (items placed, ingredient consumed).
     * Called on the main server thread, before client receives inventory updates.
     *
     * @param callback The callback.
     */
    public static void registerBrewCompletedHook(@NotNull final BrewCompletedCallback callback) {
        brewCompletedCallback = callback;
    }

    /**
     * Fire the brew-completed hook. Called by the internal brewing packet handler after placing result items.
     *
     * @param location     The brewing stand location.
     * @param recipe       The recipe that completed.
     * @param matchedSlots The base slots (0-2) that received the result.
     */
    public static void fireBrewCompleted(@NotNull final Location location,
                                         @NotNull final BrewingRecipe recipe,
                                         @NotNull final List<Integer> matchedSlots) {
        if (brewCompletedCallback != null) brewCompletedCallback.onBrewCompleted(location, recipe, matchedSlots);
    }

    /**
     * Remove all tracked Bukkit recipes from the server and clear all internal state.
     * <p>
     * Should be called on plugin disable or full recipe reload to avoid stale registrations.
     */
    public static void clear() {
        trackedBukkitKeys.forEach(key -> {
            try {
                Recipes.scheduleBukkitRecipeRemoval(key);
            } catch (Exception ignored) {
            }
        });
        trackedBukkitKeys.clear();
        recipes.clear();
        pendingRecipes.clear();
    }
}
