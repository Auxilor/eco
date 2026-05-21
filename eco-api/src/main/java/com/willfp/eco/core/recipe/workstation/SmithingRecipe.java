package com.willfp.eco.core.recipe.workstation;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.Bukkit;
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
 * are provided for all three slots a {@link SmithingTransformRecipe} is also
 * registered with Bukkit so the recipe appears in the recipe book.
 *
 * <p>All three slots ({@link Builder#template}, {@link Builder#base},
 * {@link Builder#addition}) must be set before calling {@link Builder#build()}.
 *
 * <p>Use {@link #builder(NamespacedKey, ItemStack)} to construct instances.
 */
public final class SmithingRecipe extends WorkstationRecipe {
    private final TestableItem template;
    @Nullable private final ItemStack templateDisplay;
    private final TestableItem base;
    @Nullable private final ItemStack baseDisplay;
    private final TestableItem addition;
    @Nullable private final ItemStack additionDisplay;

    private SmithingRecipe(@NotNull NamespacedKey key,
                           @Nullable ItemStack output,
                           @Nullable String permission,
                           @NotNull TestableItem template,
                           @Nullable ItemStack templateDisplay,
                           @NotNull TestableItem base,
                           @Nullable ItemStack baseDisplay,
                           @NotNull TestableItem addition,
                           @Nullable ItemStack additionDisplay) {
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

        Bukkit.addRecipe(bukkitRecipe);
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
    public static Builder builder(@NotNull NamespacedKey key, @Nullable ItemStack output) {
        return new Builder(key, output);
    }

    /**
     * Builder for {@link SmithingRecipe}.
     * <p>
     * All three slots ({@link #template}, {@link #base}, {@link #addition}) must
     * be configured before calling {@link #build()}.
     */
    public static final class Builder {
        private final NamespacedKey key;
        private final ItemStack output;
        @Nullable private String permission;
        private TestableItem template;
        @Nullable private ItemStack templateDisplay;
        private TestableItem base;
        @Nullable private ItemStack baseDisplay;
        private TestableItem addition;
        @Nullable private ItemStack additionDisplay;

        private Builder(@NotNull NamespacedKey key, @Nullable ItemStack output) {
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
        public Builder template(@NotNull TestableItem template, @Nullable ItemStack templateDisplay) {
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
        public Builder base(@NotNull TestableItem base, @Nullable ItemStack baseDisplay) {
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
        public Builder addition(@NotNull TestableItem addition, @Nullable ItemStack additionDisplay) {
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
        public Builder permission(@NotNull String permission) {
            this.permission = permission;
            return this;
        }

        /**
         * Build the {@link SmithingRecipe}.
         *
         * @return The constructed recipe.
         * @throws IllegalStateException if template, base, or addition have not been set.
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
