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
    /**
     * The brew time used when the builder is not given one.
     */
    private static final int DEFAULT_BREW_TIME = 400;

    /**
     * The required base item, placed in one of the three bottle slots.
     */
    private final TestableItem base;

    /**
     * The required ingredient, placed in the top ingredient slot.
     */
    private final TestableItem ingredient;

    /**
     * The brew duration in ticks.
     */
    private final int brewTime;

    /**
     * Create a new brewing recipe.
     *
     * @param key        Unique recipe identifier.
     * @param output     The item produced, or null.
     * @param permission The permission required to use this recipe, or null.
     * @param base       The required base item.
     * @param ingredient The required ingredient item.
     * @param brewTime   The brew duration in ticks.
     */
    private BrewingRecipe(@NotNull final NamespacedKey key,
                          @Nullable final ItemStack output,
                          @Nullable final String permission,
                          @NotNull final TestableItem base,
                          @NotNull final TestableItem ingredient,
                          final int brewTime) {
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
    public static Builder builder(@NotNull final NamespacedKey key,
                                  @Nullable final ItemStack output,
                                  @NotNull final TestableItem base,
                                  @NotNull final TestableItem ingredient) {
        return new Builder(key, output, base, ingredient);
    }

    /**
     * Builder for {@link BrewingRecipe}.
     */
    public static final class Builder {
        /**
         * The unique recipe identifier.
         */
        private final NamespacedKey key;

        /**
         * The item produced, or null.
         */
        private final ItemStack output;

        /**
         * The required base item.
         */
        private final TestableItem base;

        /**
         * The required ingredient item.
         */
        private final TestableItem ingredient;

        /**
         * The permission required to use the recipe.
         */
        @Nullable private String permission;

        /**
         * The brew duration in ticks.
         */
        private int brewTime = DEFAULT_BREW_TIME;

        /**
         * Create a new builder.
         *
         * @param key        Unique recipe identifier.
         * @param output     The item produced, or null.
         * @param base       The required base item.
         * @param ingredient The required ingredient item.
         */
        private Builder(@NotNull final NamespacedKey key,
                        @Nullable final ItemStack output,
                        @NotNull final TestableItem base,
                        @NotNull final TestableItem ingredient) {
            this.key = key;
            this.output = output;
            this.base = base;
            this.ingredient = ingredient;
        }

        /**
         * Set the brew duration.
         *
         * @param brewTime Duration in ticks. Defaults to {@code 400}.
         * @return This builder.
         */
        @NotNull
        public Builder brewTime(final int brewTime) {
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
        public Builder permission(@NotNull final String permission) {
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
