package com.willfp.eco.core.recipe.workstation;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Location;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class WorkstationRecipes {
    private static final BiMap<NamespacedKey, WorkstationRecipe> recipes = HashBiMap.create();
    private static final Set<NamespacedKey> trackedBukkitKeys = new HashSet<>();
    private static final Map<UUID, WorkstationRecipe> pendingRecipes = new HashMap<>();
    @Nullable private static Consumer<Location> brewCancelHook = null;

    private WorkstationRecipes() {}

    public static void register(@NotNull WorkstationRecipe recipe) {
        recipes.put(recipe.getKey(), recipe);
    }

    public static void trackBukkitKey(@NotNull NamespacedKey key) {
        trackedBukkitKeys.add(key);
    }

    @Nullable
    public static WorkstationRecipe getByKey(@NotNull NamespacedKey key) {
        return recipes.get(key);
    }

    @NotNull
    public static Collection<WorkstationRecipe> getAll() {
        return Collections.unmodifiableCollection(recipes.values());
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <T extends WorkstationRecipe> Collection<T> getAll(@NotNull Class<T> type) {
        return recipes.values().stream()
                .filter(type::isInstance)
                .map(r -> (T) r)
                .collect(Collectors.toList());
    }

    public static void setPendingRecipe(@NotNull UUID playerId, @NotNull WorkstationRecipe recipe) {
        pendingRecipes.put(playerId, recipe);
    }

    @Nullable
    public static WorkstationRecipe getPendingRecipe(@NotNull UUID playerId) {
        return pendingRecipes.get(playerId);
    }

    public static void clearPendingRecipe(@NotNull UUID playerId) {
        pendingRecipes.remove(playerId);
    }

    public static void registerBrewCancelHook(@NotNull Consumer<Location> hook) {
        brewCancelHook = hook;
    }

    public static void cancelPendingBrew(@NotNull Location location) {
        if (brewCancelHook != null) brewCancelHook.accept(location);
    }

    public static void clear() {
        trackedBukkitKeys.forEach(key -> {
            try { Bukkit.removeRecipe(key); } catch (Exception ignored) {}
        });
        trackedBukkitKeys.clear();
        recipes.clear();
        pendingRecipes.clear();
    }
}
