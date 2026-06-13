package com.willfp.eco.core.recipe.workstation;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A custom stonecutter recipe.
 * <p>
 * When an {@link #getInputDisplay() inputDisplay} item is provided a Bukkit
 * {@link org.bukkit.inventory.StonecuttingRecipe} is also registered so the
 * recipe appears in the stonecutter UI. The {@link com.willfp.eco.core.items.TestableItem}
 * predicate is evaluated at runtime to support custom item matching.
 *
 * <p>Use {@link #builder(NamespacedKey, ItemStack, TestableItem)} to construct instances.
 */
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

    /**
     * Get the input item predicate used to match the ingredient at runtime.
     *
     * @return The input predicate.
     */
    @NotNull
    public TestableItem getInput() {
        return input;
    }

    /**
     * Get the display item registered with Bukkit's stonecutter recipe system.
     * <p>
     * If null, no Bukkit recipe is registered and the recipe will not appear
     * in the stonecutter UI.
     *
     * @return The display item, or null.
     */
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

    /**
     * Create a new builder for a {@link StonecuttingRecipe}.
     *
     * @param key    Unique recipe identifier.
     * @param output The item produced, or null.
     * @param input  The input item predicate.
     * @return A new builder.
     */
    @NotNull
    public static Builder builder(@NotNull NamespacedKey key,
                                  @Nullable ItemStack output,
                                  @NotNull TestableItem input) {
        return new Builder(key, output, input);
    }

    /**
     * Builder for {@link StonecuttingRecipe}.
     */
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

        /**
         * Set the display item registered with Bukkit's stonecutter recipe system.
         * <p>
         * Required for the recipe to appear in the stonecutter UI.
         *
         * @param inputDisplay The display item.
         * @return This builder.
         */
        @NotNull
        public Builder inputDisplay(@NotNull ItemStack inputDisplay) {
            this.inputDisplay = inputDisplay;
            return this;
        }

        /**
         * Set the permission required to use this recipe.
         *
         * @param permission The permission node.
         * @return This builder.
         */
        @NotNull
        public Builder permission(@NotNull String permission) {
            this.permission = permission;
            return this;
        }

        /**
         * Build the {@link StonecuttingRecipe}.
         *
         * @return The constructed recipe.
         */
        @NotNull
        public StonecuttingRecipe build() {
            return new StonecuttingRecipe(key, output, permission, input, inputDisplay);
        }
    }
}
