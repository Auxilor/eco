package com.willfp.eco.core.recipe.recipes;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.recipe.Recipes;
import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import com.willfp.eco.core.recipe.parts.GroupedTestableItems;
import com.willfp.eco.core.recipe.parts.TestableStack;
import com.willfp.eco.util.ListUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Shaped 3x3 crafting recipe.
 */
public final class ShapedCraftingRecipe implements CraftingRecipe {
    /**
     * The plugin.
     */
    private final EcoPlugin plugin;

    /**
     * Recipe parts.
     */
    private final List<TestableItem> parts;

    /**
     * The key of the recipe.
     */
    private final NamespacedKey key;

    /**
     * The key of the displayed recipe.
     */
    private final NamespacedKey displayedKey;

    /**
     * The recipe's output.
     */
    private final ItemStack output;

    /**
     * The permission.
     */
    private final String permission;

    private ShapedCraftingRecipe(@NotNull final EcoPlugin plugin,
                                 @NotNull final String key,
                                 @NotNull final List<TestableItem> parts,
                                 @NotNull final ItemStack output,
                                 @Nullable final String permission) {
        this.plugin = plugin;
        this.parts = parts;
        this.key = plugin.getNamespacedKeyFactory().create(key);
        this.displayedKey = plugin.getNamespacedKeyFactory().create(key + "_displayed");
        this.output = output;
        this.permission = permission;
    }

    @Override
    public boolean test(@NotNull final ItemStack[] matrix) {
        List<ItemStack> dynamicMatrix = Arrays.asList(matrix);
        boolean matches = true;
        for (int i = 0; i < 9; i++) {
            if (!parts.get(i).matches(ListUtils.getOrNull(dynamicMatrix, i))) {
                matches = false;
            }
        }

        return matches;
    }

    @Override
    public void register() {
        Recipes.register(this);

        Bukkit.getServer().removeRecipe(this.getKey());
        Bukkit.getServer().removeRecipe(this.getDisplayedKey());

        ShapedRecipe shapedRecipe = new ShapedRecipe(this.getKey(), this.getOutput());
        shapedRecipe.shape("012", "345", "678");
        for (int i = 0; i < 9; i++) {
            if (parts.get(i) instanceof EmptyTestableItem) {
                continue;
            }

            char character = String.valueOf(i).toCharArray()[0];
            shapedRecipe.setIngredient(character, parts.get(i).getItem().getType());
        }

        if (Eco.get().getEcoPlugin().getConfigYml().getBool("displayed-recipes")) {
            ShapedRecipe displayedRecipe = new ShapedRecipe(this.getDisplayedKey(), this.getOutput());
            displayedRecipe.shape("012", "345", "678");
            for (int i = 0; i < 9; i++) {
                if (parts.get(i) instanceof EmptyTestableItem) {
                    continue;
                }

                char character = String.valueOf(i).toCharArray()[0];

                List<TestableItem> items = new ArrayList<>();
                if (parts.get(i) instanceof GroupedTestableItems group) {
                    items.addAll(group.getChildren());
                } else {
                    items.add(parts.get(i));
                }

                List<ItemStack> displayedItems = new ArrayList<>();

                for (TestableItem testableItem : items) {
                    if (testableItem instanceof TestableStack) {
                        ItemStack item = testableItem.getItem().clone();
                        ItemMeta meta = item.getItemMeta();
                        assert meta != null;

                        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                        assert lore != null;
                        lore.add("");
                        String add = Eco.get().getEcoPlugin().getLangYml().getFormattedString("multiple-in-craft");
                        add = add.replace("%amount%", String.valueOf(item.getAmount()));
                        lore.add(add);
                        meta.setLore(lore);
                        item.setItemMeta(meta);

                        displayedItems.add(item);
                    } else {
                        displayedItems.add(testableItem.getItem());
                    }
                }

                displayedRecipe.setIngredient(character, new RecipeChoice.ExactChoice(displayedItems));
            }

            Bukkit.getServer().addRecipe(displayedRecipe);
        }

        Bukkit.getServer().addRecipe(shapedRecipe);
    }

    /**
     * Get the plugin.
     *
     * @return The plugin.
     */
    public EcoPlugin getPlugin() {
        return plugin;
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

    /**
     * Get the parts.
     *
     * @return The parts.
     */
    @NotNull
    @Override
    public List<TestableItem> getParts() {
        return this.parts;
    }

    /**
     * Get the key.
     *
     * @return The key.
     */
    @NotNull
    @Override
    public NamespacedKey getKey() {
        return this.key;
    }

    /**
     * Get the displayed key.
     *
     * @return The displayed key.
     */
    @NotNull
    @Override
    public NamespacedKey getDisplayedKey() {
        return this.displayedKey;
    }

    /**
     * Get the output.
     *
     * @return The output.
     */
    @NotNull
    @Override
    public ItemStack getOutput() {
        return this.output;
    }

    /**
     * Get the permission.
     *
     * @return The permission.
     */
    @Nullable
    @Override
    public String getPermission() {
        return permission;
    }

    /**
     * Builder for recipes.
     */
    public static final class Builder {
        /**
         * The recipe parts.
         */
        private final List<TestableItem> recipeParts = new ArrayList<>(Arrays.asList(null, null, null, null, null, null, null, null, null)); // Jank

        /**
         * The output of the recipe.
         */
        private ItemStack output = null;

        /**
         * The permission for the recipe.
         */
        private String permission = null;

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
            recipeParts.set(position.getIndex(), part);
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
            recipeParts.set(position, part);
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
         * Set the permission required to craft the recipe.
         *
         * @param permission The permission.
         * @return The builder.
         */
        public Builder setPermission(@Nullable final String permission) {
            this.permission = permission;
            return this;
        }

        /**
         * Check if recipe parts are all air.
         *
         * @return If recipe parts are all air.
         */
        public boolean isAir() {
            for (TestableItem recipePart : this.recipeParts) {
                if (recipePart != null && !(recipePart instanceof EmptyTestableItem)) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Build the recipe.
         *
         * @return The built recipe.
         */
        public ShapedCraftingRecipe build() {
            for (int i = 0; i < 9; i++) {
                if (recipeParts.get(i) == null) {
                    recipeParts.set(i, new EmptyTestableItem());
                }
            }

            return new ShapedCraftingRecipe(plugin, key.toLowerCase(), recipeParts, output, permission);
        }
    }
}
