package com.willfp.eco.core.recipe.workstation;

import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.recipe.Recipes;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A custom smithing table recipe.
 * <p>
 * Mirrors the three-slot layout of a vanilla smithing table: template (top-left),
 * base (centre), and addition (top-right). Each slot is validated at runtime via
 * a {@link com.willfp.eco.core.items.TestableItem} predicate. When display items
 * are provided for all three slots, and the recipe has an output, a
 * {@link SmithingTransformRecipe} is also registered with Bukkit so the recipe appears
 * in the recipe book.
 *
 * <p>All three slots must be set via
 * {@link Builder#template(TestableItem, ItemStack)},
 * {@link Builder#base(TestableItem, ItemStack)} and
 * {@link Builder#addition(TestableItem, ItemStack)} before calling {@link Builder#build()}.
 *
 * <p>Use {@link #builder(NamespacedKey, ItemStack)} to construct instances.
 */
public final class SmithingRecipe extends WorkstationRecipe {
    /**
     * The template item predicate (top-left smithing slot).
     */
    private final TestableItem template;

    /**
     * The display item for the template slot.
     */
    @Nullable private final ItemStack templateDisplay;

    /**
     * The base item predicate (centre smithing slot).
     */
    private final TestableItem base;

    /**
     * The display item for the base slot.
     */
    @Nullable private final ItemStack baseDisplay;

    /**
     * The addition item predicate (top-right smithing slot).
     */
    private final TestableItem addition;

    /**
     * The display item for the addition slot.
     */
    @Nullable private final ItemStack additionDisplay;

    /**
     * Create a new smithing recipe.
     *
     * @param key             Unique recipe identifier.
     * @param output          The item produced, or null.
     * @param permission      The permission required to use this recipe, or null.
     * @param template        The template item predicate.
     * @param templateDisplay The display item for the template slot, or null.
     * @param base            The base item predicate.
     * @param baseDisplay     The display item for the base slot, or null.
     * @param addition        The addition item predicate.
     * @param additionDisplay The display item for the addition slot, or null.
     */
    private SmithingRecipe(@NotNull final NamespacedKey key,
                           @Nullable final ItemStack output,
                           @Nullable final String permission,
                           @NotNull final TestableItem template,
                           @Nullable final ItemStack templateDisplay,
                           @NotNull final TestableItem base,
                           @Nullable final ItemStack baseDisplay,
                           @NotNull final TestableItem addition,
                           @Nullable final ItemStack additionDisplay) {
        super(key, output, permission);
        this.template = template;
        this.templateDisplay = templateDisplay;
        this.base = base;
        this.baseDisplay = baseDisplay;
        this.addition = addition;
        this.additionDisplay = additionDisplay;
    }

    /**
     * Get the template item predicate (top-left smithing slot).
     *
     * @return The template predicate.
     */
    @NotNull
    public TestableItem getTemplate() {
        return template;
    }

    /**
     * Get the display item for the template slot registered with Bukkit.
     *
     * @return The template display item, or null if not set.
     */
    @Nullable
    public ItemStack getTemplateDisplay() {
        return templateDisplay;
    }

    /**
     * Get the base item predicate (centre smithing slot).
     *
     * @return The base predicate.
     */
    @NotNull
    public TestableItem getBase() {
        return base;
    }

    /**
     * Get the display item for the base slot registered with Bukkit.
     *
     * @return The base display item, or null if not set.
     */
    @Nullable
    public ItemStack getBaseDisplay() {
        return baseDisplay;
    }

    /**
     * Get the addition item predicate (top-right smithing slot).
     *
     * @return The addition predicate.
     */
    @NotNull
    public TestableItem getAddition() {
        return addition;
    }

    /**
     * Get the display item for the addition slot registered with Bukkit.
     *
     * @return The addition display item, or null if not set.
     */
    @Nullable
    public ItemStack getAdditionDisplay() {
        return additionDisplay;
    }

    @Override
    public void register() {
        WorkstationRecipes.register(this);

        if (getOutput() == null || templateDisplay == null || baseDisplay == null || additionDisplay == null) {
            return;
        }

        NamespacedKey key = getKey();
        SmithingTransformRecipe bukkitRecipe = new SmithingTransformRecipe(
                key,
                getOutput(),
                new RecipeChoice.ExactChoice(templateDisplay),
                new RecipeChoice.ExactChoice(baseDisplay),
                new RecipeChoice.ExactChoice(additionDisplay)
        );

        Recipes.scheduleBukkitRecipeRegistration(bukkitRecipe);
        WorkstationRecipes.trackBukkitKey(key);
    }

    /**
     * Create a new builder for a {@link SmithingRecipe}.
     *
     * @param key    Unique recipe identifier.
     * @param output The item produced, or null.
     * @return A new builder.
     */
    @NotNull
    public static Builder builder(@NotNull final NamespacedKey key, @Nullable final ItemStack output) {
        return new Builder(key, output);
    }

    /**
     * Builder for {@link SmithingRecipe}.
     * <p>
     * All three slots must be configured via {@link #template(TestableItem, ItemStack)},
     * {@link #base(TestableItem, ItemStack)} and {@link #addition(TestableItem, ItemStack)}
     * before calling {@link #build()}.
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
         * The permission required to use the recipe.
         */
        @Nullable private String permission;

        /**
         * The template item predicate.
         */
        private TestableItem template;

        /**
         * The display item for the template slot.
         */
        @Nullable private ItemStack templateDisplay;

        /**
         * The base item predicate.
         */
        private TestableItem base;

        /**
         * The display item for the base slot.
         */
        @Nullable private ItemStack baseDisplay;

        /**
         * The addition item predicate.
         */
        private TestableItem addition;

        /**
         * The display item for the addition slot.
         */
        @Nullable private ItemStack additionDisplay;

        /**
         * Create a new builder.
         *
         * @param key    Unique recipe identifier.
         * @param output The item produced, or null.
         */
        private Builder(@NotNull final NamespacedKey key, @Nullable final ItemStack output) {
            this.key = key;
            this.output = output;
        }

        /**
         * Set the template slot ingredient.
         *
         * @param template        The item predicate.
         * @param templateDisplay The display item for Bukkit registration, or null.
         * @return This builder.
         */
        @NotNull
        public Builder template(@NotNull final TestableItem template, @Nullable final ItemStack templateDisplay) {
            this.template = template;
            this.templateDisplay = templateDisplay;
            return this;
        }

        /**
         * Set the base slot ingredient.
         *
         * @param base        The item predicate.
         * @param baseDisplay The display item for Bukkit registration, or null.
         * @return This builder.
         */
        @NotNull
        public Builder base(@NotNull final TestableItem base, @Nullable final ItemStack baseDisplay) {
            this.base = base;
            this.baseDisplay = baseDisplay;
            return this;
        }

        /**
         * Set the addition slot ingredient.
         *
         * @param addition        The item predicate.
         * @param additionDisplay The display item for Bukkit registration, or null.
         * @return This builder.
         */
        @NotNull
        public Builder addition(@NotNull final TestableItem addition, @Nullable final ItemStack additionDisplay) {
            this.addition = addition;
            this.additionDisplay = additionDisplay;
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
         * Build the {@link SmithingRecipe}.
         *
         * @return The constructed recipe.
         * @throws IllegalStateException If template, base, or addition have not been set.
         */
        @NotNull
        public SmithingRecipe build() {
            if (template == null || base == null || addition == null) {
                throw new IllegalStateException("SmithingRecipe requires template, base, and addition to be set");
            }
            return new SmithingRecipe(key, output, permission, template, templateDisplay, base, baseDisplay, addition, additionDisplay);
        }
    }
}
