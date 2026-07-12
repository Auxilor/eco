package com.willfp.eco.core.recipe.workstation;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A custom grindstone recipe.
 * <p>
 * Matches when one or two qualifying items are placed in the grindstone slots.
 * At minimum {@link #getItem1() item1} must be present; {@link #getItem2() item2}
 * is optional and represents the second input slot. Detection is packet-based.
 *
 * <p>Use {@link #builder(NamespacedKey, ItemStack, TestableItem)} to construct instances.
 */
public final class GrindstoneRecipe extends WorkstationRecipe {
    private final TestableItem item1;
    @Nullable private final TestableItem item2;

    private GrindstoneRecipe(@NotNull final NamespacedKey key,
                             @Nullable final ItemStack output,
                             @Nullable final String permission,
                             @NotNull final TestableItem item1,
                             @Nullable final TestableItem item2) {
        super(key, output, permission);
        this.item1 = item1;
        this.item2 = item2;
    }

    /**
     * Get the required item for the first grindstone input slot.
     *
     * @return The item predicate.
     */
    @NotNull
    public TestableItem getItem1() {
        return item1;
    }

    /**
     * Get the optional item for the second grindstone input slot.
     *
     * @return The item predicate, or null if only one input is required.
     */
    @Nullable
    public TestableItem getItem2() {
        return item2;
    }

    @Override
    public void register() {
        WorkstationRecipes.register(this);
    }

    /**
     * Create a new builder for a {@link GrindstoneRecipe}.
     *
     * @param key    Unique recipe identifier.
     * @param output The item produced, or null.
     * @param item1  The required first input item predicate.
     * @return A new builder.
     */
    @NotNull
    public static Builder builder(@NotNull final NamespacedKey key,
                                  @Nullable final ItemStack output,
                                  @NotNull final TestableItem item1) {
        return new Builder(key, output, item1);
    }

    /**
     * Builder for {@link GrindstoneRecipe}.
     */
    public static final class Builder {
        private final NamespacedKey key;
        private final ItemStack output;
        private final TestableItem item1;
        @Nullable private TestableItem item2;
        @Nullable private String permission;

        private Builder(@NotNull final NamespacedKey key,
                        @Nullable final ItemStack output,
                        @NotNull final TestableItem item1) {
            this.key = key;
            this.output = output;
            this.item1 = item1;
        }

        /**
         * Set the optional second input item.
         *
         * @param item2 The item predicate, or null for no second input requirement.
         * @return This builder.
         */
        @NotNull
        public Builder item2(@Nullable final TestableItem item2) {
            this.item2 = item2;
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
         * Build the {@link GrindstoneRecipe}.
         *
         * @return The constructed recipe.
         */
        @NotNull
        public GrindstoneRecipe build() {
            return new GrindstoneRecipe(key, output, permission, item1, item2);
        }
    }
}
