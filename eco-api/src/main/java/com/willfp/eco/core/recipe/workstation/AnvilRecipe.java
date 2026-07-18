package com.willfp.eco.core.recipe.workstation;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A custom anvil recipe.
 * <p>
 * Matches when a player places a qualifying {@link #getBase() base} item (and
 * optionally a {@link #getMaterial() material} item) in an anvil. The packet
 * handler validates both inputs against their {@link com.willfp.eco.core.items.TestableItem}
 * predicates at interact-time.
 *
 * <p>Use {@link #builder(NamespacedKey, ItemStack, TestableItem)} to construct instances.
 */
public final class AnvilRecipe extends WorkstationRecipe {
    private static final int DEFAULT_REPAIR_COST = 1;

    private final TestableItem base;
    @Nullable private final TestableItem material;
    @Nullable private final ItemStack baseDisplay;
    @Nullable private final ItemStack materialDisplay;
    @Nullable private final String resultName;
    private final int repairCost;

    private AnvilRecipe(@NotNull final NamespacedKey key,
                        @Nullable final ItemStack output,
                        @Nullable final String permission,
                        @NotNull final TestableItem base,
                        @Nullable final TestableItem material,
                        @Nullable final ItemStack baseDisplay,
                        @Nullable final ItemStack materialDisplay,
                        @Nullable final String resultName,
                        final int repairCost) {
        super(key, output, permission);
        this.base = base;
        this.material = material;
        this.baseDisplay = baseDisplay;
        this.materialDisplay = materialDisplay;
        this.resultName = resultName;
        this.repairCost = repairCost;
    }

    /**
     * Get the required base item placed in the left anvil slot.
     *
     * @return The base item predicate.
     */
    @NotNull
    public TestableItem getBase() {
        return base;
    }

    /**
     * Get the optional material placed in the right anvil slot.
     *
     * @return The material item predicate, or null if no material is required.
     */
    @Nullable
    public TestableItem getMaterial() {
        return material;
    }

    /**
     * Get the display item registered for the base slot.
     *
     * @return The display item, or null if not set.
     */
    @Nullable
    public ItemStack getBaseDisplay() {
        return baseDisplay;
    }

    /**
     * Get the display item registered for the material slot.
     *
     * @return The display item, or null if not set.
     */
    @Nullable
    public ItemStack getMaterialDisplay() {
        return materialDisplay;
    }

    /**
     * Get the display name applied to the result item.
     *
     * @return The result name, or null to leave the output name unchanged.
     */
    @Nullable
    public String getResultName() {
        return resultName;
    }

    /**
     * Get the experience level cost of this recipe.
     *
     * @return The repair cost in levels.
     */
    public int getRepairCost() {
        return repairCost;
    }

    @Override
    public void register() {
        WorkstationRecipes.register(this);
    }

    /**
     * Create a new builder for an {@link AnvilRecipe}.
     *
     * @param key    Unique recipe identifier.
     * @param output The item produced, or null.
     * @param base   The required base item.
     * @return A new builder.
     */
    @NotNull
    public static Builder builder(@NotNull final NamespacedKey key,
                                  @Nullable final ItemStack output,
                                  @NotNull final TestableItem base) {
        return new Builder(key, output, base);
    }

    /**
     * Builder for {@link AnvilRecipe}.
     */
    public static final class Builder {
        private final NamespacedKey key;
        private final ItemStack output;
        private final TestableItem base;
        @Nullable private TestableItem material;
        @Nullable private ItemStack baseDisplay;
        @Nullable private ItemStack materialDisplay;
        @Nullable private String resultName;
        private int repairCost = DEFAULT_REPAIR_COST;
        @Nullable private String permission;

        private Builder(@NotNull final NamespacedKey key,
                        @Nullable final ItemStack output,
                        @NotNull final TestableItem base) {
            this.key = key;
            this.output = output;
            this.base = base;
        }

        /**
         * Set the material item for the right anvil slot.
         *
         * @param material The material predicate, or null for no material requirement.
         * @return This builder.
         */
        @NotNull
        public Builder material(@Nullable final TestableItem material) {
            this.material = material;
            return this;
        }

        /**
         * Set the display item for the base slot.
         *
         * @param baseDisplay The display item.
         * @return This builder.
         */
        @NotNull
        public Builder baseDisplay(@Nullable final ItemStack baseDisplay) {
            this.baseDisplay = baseDisplay;
            return this;
        }

        /**
         * Set the display item for the material slot.
         *
         * @param materialDisplay The display item.
         * @return This builder.
         */
        @NotNull
        public Builder materialDisplay(@Nullable final ItemStack materialDisplay) {
            this.materialDisplay = materialDisplay;
            return this;
        }

        /**
         * Set the display name applied to the result item.
         *
         * @param resultName The name to apply, or null to leave the output name unchanged.
         * @return This builder.
         */
        @NotNull
        public Builder resultName(@Nullable final String resultName) {
            this.resultName = resultName;
            return this;
        }

        /**
         * Set the experience level cost of this recipe.
         *
         * @param repairCost Cost in levels. Defaults to {@value DEFAULT_REPAIR_COST}.
         * @return This builder.
         */
        @NotNull
        public Builder repairCost(final int repairCost) {
            this.repairCost = repairCost;
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
         * Build the {@link AnvilRecipe}.
         *
         * @return The constructed recipe.
         */
        @NotNull
        public AnvilRecipe build() {
            return new AnvilRecipe(key, output, permission, base, material, baseDisplay, materialDisplay, resultName, repairCost);
        }
    }
}
