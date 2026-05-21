package com.willfp.eco.core.recipe.workstation;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A custom brewing stand recipe.
 * <p>
 * Matches when a qualifying {@link #getIngredient() ingredient} is placed above
 * a qualifying {@link #getBase() base} potion in a brewing stand. Detection is
 * packet-based; the vanilla brewing stand is not used directly.
 *
 * <p>Use {@link #builder(NamespacedKey, ItemStack, TestableItem, TestableItem)} to construct instances.
 */
public final class BrewingRecipe extends WorkstationRecipe {
    private static final int DEFAULT_BREW_TIME = 400;

    private final TestableItem base;
    private final TestableItem ingredient;
    private final int brewTime;

    private BrewingRecipe(@NotNull NamespacedKey key,
                          @Nullable ItemStack output,
                          @Nullable String permission,
                          @NotNull TestableItem base,
                          @NotNull TestableItem ingredient,
                          int brewTime) {
        super(key, output, permission);
        this.base = base;
        this.ingredient = ingredient;
        this.brewTime = brewTime;
    }

    /**
     * Get the required base item placed in one of the three bottle slots.
     *
     * @return The base item predicate.
     */
    @NotNull
    public TestableItem getBase() {
        return base;
    }

    /**
     * Get the required ingredient placed in the top ingredient slot.
     *
     * @return The ingredient item predicate.
     */
    @NotNull
    public TestableItem getIngredient() {
        return ingredient;
    }

    /**
     * Get the brew duration in ticks.
     *
     * @return Brew time in ticks.
     */
    public int getBrewTime() {
        return brewTime;
    }

    @Override
    public void register() {
        WorkstationRecipes.register(this);
    }

    /**
     * Create a new builder for a {@link BrewingRecipe}.
     *
     * @param key        Unique recipe identifier.
     * @param output     The item produced, or null.
     * @param base       The required base item predicate.
     * @param ingredient The required ingredient item predicate.
     * @return A new builder.
     */
    @NotNull
    public static Builder builder(@NotNull NamespacedKey key,
                                  @Nullable ItemStack output,
                                  @NotNull TestableItem base,
                                  @NotNull TestableItem ingredient) {
        return new Builder(key, output, base, ingredient);
    }

    /**
     * Builder for {@link BrewingRecipe}.
     */
    public static final class Builder {
        private final NamespacedKey key;
        private final ItemStack output;
        private final TestableItem base;
        private final TestableItem ingredient;
        @Nullable private String permission;
        private int brewTime = DEFAULT_BREW_TIME;

        private Builder(@NotNull NamespacedKey key,
                        @Nullable ItemStack output,
                        @NotNull TestableItem base,
                        @NotNull TestableItem ingredient) {
            this.key = key;
            this.output = output;
            this.base = base;
            this.ingredient = ingredient;
        }

        /**
         * Set the brew duration.
         *
         * @param brewTime Duration in ticks. Defaults to {@value DEFAULT_BREW_TIME}.
         * @return This builder.
         */
        @NotNull
        public Builder brewTime(int brewTime) {
            this.brewTime = brewTime;
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
         * Build the {@link BrewingRecipe}.
         *
         * @return The constructed recipe.
         */
        @NotNull
        public BrewingRecipe build() {
            return new BrewingRecipe(key, output, permission, base, ingredient, brewTime);
        }
    }
}
