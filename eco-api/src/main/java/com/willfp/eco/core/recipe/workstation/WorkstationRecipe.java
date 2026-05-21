package com.willfp.eco.core.recipe.workstation;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class WorkstationRecipe {
    private final NamespacedKey key;
    private final ItemStack output;
    @Nullable private final String permission;

    protected WorkstationRecipe(@NotNull NamespacedKey key,
                                @Nullable ItemStack output,
                                @Nullable String permission) {
        this.key = key;
        this.output = output;
        this.permission = permission;
    }

    @NotNull public NamespacedKey getKey() { return key; }
    @Nullable public ItemStack getOutput() { return output; }
    @Nullable public String getPermission() { return permission; }

    public abstract void register();
}
