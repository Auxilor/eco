package com.willfp.eco.core.recipe.workstation;

import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.recipe.Recipes;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmokingRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A custom smelting recipe for furnace-type workstations.
 * <p>
 * Supports all four cooking workstations via {@link SmeltingType}. When
 * an {@link #getInputDisplay() inputDisplay} item is provided, a matching
 * Bukkit cooking recipe is registered so the recipe appears in the recipe book.
 * The {@link com.willfp.eco.core.items.TestableItem} predicate is used at
 * runtime to validate the actual item in the input slot, allowing custom item
 * matching beyond what Bukkit's exact-choice supports.
 *
 * <p>Default cook times per type:
 * <ul>
 *   <li>{@link SmeltingType#FURNACE} - 200 ticks</li>
 *   <li>{@link SmeltingType#BLAST_FURNACE} / {@link SmeltingType#SMOKER} - 100 ticks</li>
 *   <li>{@link SmeltingType#CAMPFIRE} - 600 ticks</li>
 * </ul>
 * Pass a non-negative value to {@link Builder#cookTime(int)} to override the default.
 *
 * <p>Use {@link #builder(NamespacedKey, ItemStack, TestableItem, SmeltingType)} to construct instances.
 */
public final class SmeltingRecipe extends WorkstationRecipe {
    private static final int DEFAULT_FURNACE = 200;
    private static final int DEFAULT_BLAST_SMOKER = 100;
    private static final int DEFAULT_CAMPFIRE = 600;

    private final TestableItem input;
    @Nullable private final ItemStack inputDisplay;
    private final SmeltingType smeltingType;
    private final int cookTime;
    private final float experience;

    private SmeltingRecipe(@NotNull final NamespacedKey key,
                           @Nullable final ItemStack output,
                           @Nullable final String permission,
                           @NotNull final TestableItem input,
                           @Nullable final ItemStack inputDisplay,
                           @NotNull final SmeltingType smeltingType,
                           final int cookTime,
                           final float experience) {
        super(key, output, permission);
        this.input = input;
        this.inputDisplay = inputDisplay;
        this.smeltingType = smeltingType;
        this.cookTime = cookTime;
        this.experience = experience;
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
     * Get the display item registered with Bukkit's recipe system.
     * <p>
     * If null, no Bukkit recipe is registered and the recipe will not appear
     * in the recipe book.
     *
     * @return The display item, or null.
     */
    @Nullable
    public ItemStack getInputDisplay() {
        return inputDisplay;
    }

    /**
     * Get the smelting workstation type this recipe targets.
     *
     * @return The smelting type.
     */
    @NotNull
    public SmeltingType getSmeltingType() {
        return smeltingType;
    }

    /**
     * Get the cook time as configured, in ticks.
     * <p>
     * A value of {@code -1} means the type-specific default will be used
     * when registering with Bukkit. Use {@link #resolvedCookTime()} for the
     * effective value.
     *
     * @return The configured cook time in ticks, or {@code -1} for the default.
     */
    public int getCookTime() {
        return cookTime;
    }

    /**
     * Get the experience awarded when the recipe completes.
     *
     * @return Experience points.
     */
    public float getExperience() {
        return experience;
    }

    private int resolvedCookTime() {
        if (cookTime >= 0) {
            return cookTime;
        }
        return switch (smeltingType) {
            case FURNACE -> DEFAULT_FURNACE;
            case BLAST_FURNACE -> DEFAULT_BLAST_SMOKER;
            case SMOKER -> DEFAULT_BLAST_SMOKER;
            case CAMPFIRE -> DEFAULT_CAMPFIRE;
        };
    }

    @Override
    public void register() {
        WorkstationRecipes.register(this);

        if (getOutput() == null || inputDisplay == null) {
            return;
        }

        RecipeChoice ingredient = new RecipeChoice.ExactChoice(inputDisplay);
        int time = resolvedCookTime();
        NamespacedKey key = getKey();
        ItemStack output = getOutput();

        CookingRecipe<?> bukkitRecipe = switch (smeltingType) {
            case FURNACE -> new FurnaceRecipe(key, output, ingredient, experience, time);
            case BLAST_FURNACE -> new BlastingRecipe(key, output, ingredient, experience, time);
            case SMOKER -> new SmokingRecipe(key, output, ingredient, experience, time);
            case CAMPFIRE -> new CampfireRecipe(key, output, ingredient, experience, time);
        };

        Recipes.scheduleBukkitRecipeRegistration(bukkitRecipe);
        WorkstationRecipes.trackBukkitKey(key);
    }

    /**
     * Create a new builder for a {@link SmeltingRecipe}.
     *
     * @param key          Unique recipe identifier.
     * @param output       The item produced, or null.
     * @param input        The input item predicate.
     * @param smeltingType The workstation type.
     * @return A new builder.
     */
    @NotNull
    public static Builder builder(@NotNull final NamespacedKey key,
                                  @Nullable final ItemStack output,
                                  @NotNull final TestableItem input,
                                  @NotNull final SmeltingType smeltingType) {
        return new Builder(key, output, input, smeltingType);
    }

    /**
     * Builder for {@link SmeltingRecipe}.
     */
    public static final class Builder {
        private final NamespacedKey key;
        private final ItemStack output;
        private final TestableItem input;
        private final SmeltingType smeltingType;
        @Nullable private ItemStack inputDisplay;
        @Nullable private String permission;
        private int cookTime = -1;
        private float experience = 0f;

        private Builder(@NotNull final NamespacedKey key,
                        @Nullable final ItemStack output,
                        @NotNull final TestableItem input,
                        @NotNull final SmeltingType smeltingType) {
            this.key = key;
            this.output = output;
            this.input = input;
            this.smeltingType = smeltingType;
        }

        /**
         * Set the cook time override.
         *
         * @param cookTime Duration in ticks. Pass {@code -1} (default) to use the
         *                 type-specific default.
         * @return This builder.
         */
        @NotNull
        public Builder cookTime(final int cookTime) {
            this.cookTime = cookTime;
            return this;
        }

        /**
         * Set the experience awarded on completion.
         *
         * @param experience Experience points. Defaults to {@code 0}.
         * @return This builder.
         */
        @NotNull
        public Builder experience(final float experience) {
            this.experience = experience;
            return this;
        }

        /**
         * Set the display item registered with Bukkit's recipe system.
         * <p>
         * Required for the recipe to appear in the recipe book. The display item
         * is matched via {@link RecipeChoice.ExactChoice}.
         *
         * @param inputDisplay The display item.
         * @return This builder.
         */
        @NotNull
        public Builder inputDisplay(@NotNull final ItemStack inputDisplay) {
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
        public Builder permission(@NotNull final String permission) {
            this.permission = permission;
            return this;
        }

        /**
         * Build the {@link SmeltingRecipe}.
         *
         * @return The constructed recipe.
         */
        @NotNull
        public SmeltingRecipe build() {
            return new SmeltingRecipe(key, output, permission, input, inputDisplay, smeltingType, cookTime, experience);
        }
    }
}
