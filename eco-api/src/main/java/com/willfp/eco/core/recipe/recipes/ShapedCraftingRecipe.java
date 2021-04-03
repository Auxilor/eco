package com.willfp.eco.core.recipe.recipes;

import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.recipe.Recipes;
import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class ShapedCraftingRecipe extends PluginDependent implements CraftingRecipe {
    /**
     * Recipe parts.
     */
    @Getter
    private final TestableItem[] parts;

    /**
     * The key of the recipe.
     */
    @Getter
    private final NamespacedKey key;

    /**
     * The key of the displayed recipe.
     */
    @Getter
    private final NamespacedKey displayedKey;

    /**
     * The recipe's output.
     */
    @Getter
    private final ItemStack output;

    private ShapedCraftingRecipe(@NotNull final EcoPlugin plugin,
                                 @NotNull final String key,
                                 @NotNull final TestableItem[] parts,
                                 @NotNull final ItemStack output) {
        super(plugin);

        this.parts = parts;
        this.key = plugin.getNamespacedKeyFactory().create(key);
        this.displayedKey = plugin.getNamespacedKeyFactory().create(key + "_displayed");
        this.output = output;
    }

    @Override
    public Material getMaterialAtIndex(final int index) {
        return parts[index].getItem().getType();
    }

    @Override
    public ItemStack getDisplayedAtIndex(final int index) {
        return parts[index].getItem();
    }

    @Override
    public boolean test(@NotNull final ItemStack[] matrix) {
        boolean matches = true;
        for (int i = 0; i < 9; i++) {
            if (!parts[i].matches(matrix[i])) {
                matches = false;
            }
        }

        return matches;
    }

    @Override
    public void register() {
        Recipes.register(this);
    }

    @Override
    public String toString() {
        return "EcoShapedRecipe{"
                + "parts=" + Arrays.toString(parts)
                + ", key='" + key + '\''
                + ", output=" + output
                + '}';
    }

    /**
     * Create a new recipe builder.
     *
     * @param plugin The plugin that owns the recipe.
     * @param key    The recipe key.
     * @return A new builder.
     */
    public static Builder builder(@NotNull final EcoPlugin plugin,
                                  @NotNull final String key) {
        return new Builder(plugin, key);
    }

    public static final class Builder {
        /**
         * The recipe parts.
         */
        private final TestableItem[] recipeParts = new TestableItem[9];

        /**
         * The output of the recipe.
         */
        private ItemStack output = null;

        /**
         * The key of the recipe.
         */
        private final String key;

        /**
         * The plugin that created the recipe.
         */
        private final EcoPlugin plugin;

        /**
         * Create a new recipe builder.
         *
         * @param plugin The plugin that owns the recipe.
         * @param key    The recipe key.
         */
        private Builder(@NotNull final EcoPlugin plugin,
                        @NotNull final String key) {
            this.key = key;
            this.plugin = plugin;
        }

        /**
         * Set a recipe part.
         *
         * @param position The position of the recipe within a crafting matrix.
         * @param part     The part of the recipe.
         * @return The builder.
         */
        public Builder setRecipePart(@NotNull final RecipePosition position,
                                     @NotNull final TestableItem part) {
            this.recipeParts[position.getIndex()] = part;
            return this;
        }

        /**
         * Set a recipe part.
         *
         * @param position The position of the recipe within a crafting matrix.
         * @param part     The part of the recipe.
         * @return The builder.
         */
        public Builder setRecipePart(final int position,
                                     @NotNull final TestableItem part) {
            this.recipeParts[position] = part;
            return this;
        }

        /**
         * Set the output of the recipe.
         *
         * @param output The output.
         * @return The builder.
         */
        public Builder setOutput(@NotNull final ItemStack output) {
            this.output = output;
            return this;
        }

        /**
         * Build the recipe.
         *
         * @return The built recipe.
         */
        public ShapedCraftingRecipe build() {
            for (int i = 0; i < 9; i++) {
                if (recipeParts[i] == null) {
                    recipeParts[i] = new EmptyTestableItem();
                }
            }

            return new ShapedCraftingRecipe(plugin, key.toLowerCase(), recipeParts, output);
        }
    }
}
