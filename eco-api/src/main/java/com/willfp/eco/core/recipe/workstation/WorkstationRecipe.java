package com.willfp.eco.core.recipe.workstation;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for all workstation-based recipes.
 * <p>
 * Workstation recipes are not standard crafting table recipes; they cover
 * anvils, brewing stands, grindstones, smithing tables, furnaces, and similar
 * workstations. Subclasses define the ingredient requirements and register
 * themselves with the server via {@link #register()}.
 */
public abstract class WorkstationRecipe {
    private final NamespacedKey key;
    private final ItemStack output;
    @Nullable private final String permission;

    /**
     * @param key        Unique identifier for this recipe.
     * @param output     The item produced by this recipe, or null for recipes
     *                   that do not produce a fixed output item.
     * @param permission Permission node required to use this recipe, or null
     *                   if no permission is required.
     */
    protected WorkstationRecipe(@NotNull NamespacedKey key,
                                @Nullable ItemStack output,
                                @Nullable String permission) {
        this.key = key;
        this.output = output;
        this.permission = permission;
    }

    /**
     * Get the unique key for this recipe.
     *
     * @return The key.
     */
    @NotNull public NamespacedKey getKey() { return key; }

    /**
     * Get the output item produced by this recipe.
     *
     * @return The output, or null if this recipe has no fixed output.
     */
    @Nullable public ItemStack getOutput() { return output; }

    /**
     * Get the permission required to use this recipe.
     *
     * @return The permission node, or null if no permission is required.
     */
    @Nullable public String getPermission() { return permission; }

    /**
     * Register this recipe with {@link WorkstationRecipes} and, where
     * applicable, with the Bukkit recipe system.
     */
    public abstract void register();
}
