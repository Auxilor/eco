package com.willfp.eco.core.recipe.workstation;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class StonecuttingRecipe extends WorkstationRecipe {
    private final TestableItem input;
    @Nullable private final ItemStack inputDisplay;

    private StonecuttingRecipe(@NotNull NamespacedKey key,
                               @Nullable ItemStack output,
                               @Nullable String permission,
                               @NotNull TestableItem input,
                               @Nullable ItemStack inputDisplay) {
        super(key, output, permission);
        this.input = input;
        this.inputDisplay = inputDisplay;
    }

    @NotNull
    public TestableItem getInput() {
        return input;
    }

    @Nullable
    public ItemStack getInputDisplay() {
        return inputDisplay;
    }

    @Override
    public void register() {
        WorkstationRecipes.register(this);

        if (getOutput() == null || inputDisplay == null) {
            return;
        }

        NamespacedKey key = getKey();
        org.bukkit.inventory.StonecuttingRecipe bukkitRecipe = new org.bukkit.inventory.StonecuttingRecipe(
                key,
                getOutput().clone(),
                new RecipeChoice.ExactChoice(inputDisplay)
        );

        Bukkit.addRecipe(bukkitRecipe);
        WorkstationRecipes.trackBukkitKey(key);
    }

    @NotNull
    public static Builder builder(@NotNull NamespacedKey key,
                                  @Nullable ItemStack output,
                                  @NotNull TestableItem input) {
        return new Builder(key, output, input);
    }

    public static final class Builder {
        private final NamespacedKey key;
        private final ItemStack output;
        private final TestableItem input;
        @Nullable private ItemStack inputDisplay;
        @Nullable private String permission;

        private Builder(@NotNull NamespacedKey key,
                        @Nullable ItemStack output,
                        @NotNull TestableItem input) {
            this.key = key;
            this.output = output;
            this.input = input;
        }

        @NotNull
        public Builder inputDisplay(@NotNull ItemStack inputDisplay) {
            this.inputDisplay = inputDisplay;
            return this;
        }

        @NotNull
        public Builder permission(@NotNull String permission) {
            this.permission = permission;
            return this;
        }

        @NotNull
        public StonecuttingRecipe build() {
            return new StonecuttingRecipe(key, output, permission, input, inputDisplay);
        }
    }
}
