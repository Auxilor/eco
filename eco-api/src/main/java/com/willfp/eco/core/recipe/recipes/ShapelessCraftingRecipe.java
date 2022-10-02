package com.willfp.eco.core.recipe.recipes;

import com.google.common.annotations.Beta;
import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.recipe.Recipes;
import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import com.willfp.eco.core.recipe.parts.GroupedTestableItems;
import com.willfp.eco.core.recipe.parts.TestableStack;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Shapeless crafting recipe.
 */
@Beta
public final class ShapelessCraftingRecipe implements CraftingRecipe {
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

    private ShapelessCraftingRecipe(@NotNull final EcoPlugin plugin,
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

    /**
     * Make a new test.
     *
     * @return The test.
     */
    @NotNull
    public RecipeTest newTest() {
        return new RecipeTest(this);
    }

    @Override
    public boolean test(@NotNull final ItemStack[] matrix) {
        RecipeTest test = newTest();

        for (ItemStack stack : matrix) {
            if (test.matchAndRemove(stack) == null) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void register() {
        Recipes.register(this);

        Bukkit.getServer().removeRecipe(this.getKey());
        Bukkit.getServer().removeRecipe(this.getDisplayedKey());

        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(this.getKey(), this.getOutput());
        for (TestableItem part : parts) {
            shapelessRecipe.addIngredient(part.getItem().getType());
        }

        if (Eco.get().getEcoPlugin().getConfigYml().getBool("displayed-recipes")) {
            ShapelessRecipe displayedRecipe = new ShapelessRecipe(this.getDisplayedKey(), this.getOutput());
            for (TestableItem part : parts) {
                List<TestableItem> items = new ArrayList<>();
                if (part instanceof GroupedTestableItems group) {
                    items.addAll(group.getChildren());
                } else {
                    items.add(part);
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

                displayedRecipe.addIngredient(new RecipeChoice.ExactChoice(displayedItems));
            }

            Bukkit.getServer().addRecipe(displayedRecipe);
        }

        Bukkit.getServer().addRecipe(shapelessRecipe);
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
        private final List<TestableItem> recipeParts = new ArrayList<>();

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
         * Add a recipe part.
         *
         * @param part The part of the recipe.
         * @return The builder.
         */
        public Builder addRecipePart(@NotNull final TestableItem part) {
            recipeParts.add(part);
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
        public ShapelessCraftingRecipe build() {
            return new ShapelessCraftingRecipe(plugin, key.toLowerCase(), recipeParts, output, permission);
        }
    }

    /**
     * Test for shapeless recipes.
     */
    public static final class RecipeTest {
        /**
         * The remaining items left to be found.
         */
        private final List<TestableItem> remaining;

        private RecipeTest(@NotNull final ShapelessCraftingRecipe recipe) {
            this.remaining = new ArrayList<>(recipe.getParts());
        }

        /**
         * If the item is in the recipe, remove it from the remaining items to test and
         * return the matching item.
         *
         * @param itemStack The item.
         * @return The matching item, or null if no match was found.
         */
        @Nullable
        public TestableItem matchAndRemove(@NotNull final ItemStack itemStack) {
            if (remaining.isEmpty() && !(new EmptyTestableItem().matches(itemStack))) {
                return null;
            }

            Optional<TestableItem> match = remaining.stream()
                    .filter(item -> item.matches(itemStack))
                    .findFirst();

            match.ifPresent(remaining::remove);

            return match.orElse(null);
        }
    }
}
