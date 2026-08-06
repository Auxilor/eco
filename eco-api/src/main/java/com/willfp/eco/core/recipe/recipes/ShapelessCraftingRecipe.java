package com.willfp.eco.core.recipe.recipes;

import com.google.common.annotations.Beta;
import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.recipe.Recipes;
import com.willfp.eco.core.recipe.parts.EmptyTestableItem;
import com.willfp.eco.core.recipe.parts.GroupedTestableItems;
import com.willfp.eco.core.recipe.parts.TestableStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Shapeless crafting recipe.
 * <p>
 * The parts list holds one entry per required ingredient, in no particular order, and
 * unlike {@link ShapedCraftingRecipe} it is not padded to nine entries. A matrix matches
 * when every non-empty stack in it consumes a distinct part and no parts are left over.
 * <p>
 * Instances are created through {@link #builder(EcoPlugin, String)} and are only live
 * once {@link #register()} has been called.
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

    /**
     * Whether this recipe also fires inside the vanilla Crafter block.
     */
    private final boolean crafterSupported;

    /**
     * Create a new shapeless crafting recipe.
     *
     * @param plugin           The plugin that owns the recipe.
     * @param key              The recipe key, namespaced under the plugin's ID.
     * @param parts            The recipe parts, one per required ingredient.
     * @param output           The output.
     * @param permission       The permission required to craft, or null for none.
     * @param crafterSupported Whether the recipe also fires in the vanilla Crafter block.
     */
    private ShapelessCraftingRecipe(@NotNull final EcoPlugin plugin,
                                    @NotNull final String key,
                                    @NotNull final List<TestableItem> parts,
                                    @NotNull final ItemStack output,
                                    @Nullable final String permission,
                                    final boolean crafterSupported) {
        this.plugin = plugin;
        this.parts = parts;
        this.key = plugin.getNamespacedKeyFactory().create(key);
        this.displayedKey = plugin.getNamespacedKeyFactory().create(key + "_displayed");
        this.output = output;
        this.permission = permission;
        this.crafterSupported = crafterSupported;
    }

    @Override
    public boolean isCrafterSupported() {
        return this.crafterSupported;
    }

    /**
     * Make a new test, holding a fresh mutable copy of this recipe's parts.
     *
     * @return The test.
     */
    @NotNull
    public RecipeTest newTest() {
        return new RecipeTest(this);
    }

    @Override
    public boolean test(@Nullable final ItemStack[] matrix) {
        if (matrix == null) {
            return false;
        }

        RecipeTest test = newTest();

        for (ItemStack stack : matrix) {
            if (stack == null || stack.getType().isAir() || stack.getAmount() <= 0) {
                continue;
            }

            if (test.matchAndRemove(stack) == null) {
                return false;
            }
        }

        return test.remaining.isEmpty();
    }

    @Override
    public void register() {
        Recipes.register(this);

        Recipes.scheduleBukkitRecipeRemoval(this.getKey());
        Recipes.scheduleBukkitRecipeRemoval(this.getDisplayedKey());

        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(this.getKey(), this.getOutput());
        for (TestableItem part : parts) {
            // Mirror ShapedCraftingRecipe: skip empty/AIR parts so Bukkit
            // doesn't reject the recipe with IllegalArgumentException.
            if (part instanceof EmptyTestableItem) {
                continue;
            }
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

            Recipes.scheduleBukkitRecipeRegistration(displayedRecipe);
        }

        Recipes.scheduleBukkitRecipeRegistration(shapelessRecipe);

        if (this.crafterSupported) {
            NamespacedKey crafterKey = new NamespacedKey(
                    this.getKey().getNamespace(),
                    this.getKey().getKey() + "_crafter"
            );
            Recipes.scheduleBukkitRecipeRemoval(crafterKey);

            ShapelessRecipe crafterRecipe = new ShapelessRecipe(crafterKey, this.getOutput());
            for (TestableItem part : parts) {
                if (part instanceof EmptyTestableItem) {
                    continue;
                }
                crafterRecipe.addIngredient(new RecipeChoice.ExactChoice(part.getItem().clone()));
            }
            Recipes.scheduleBukkitRecipeRegistration(crafterRecipe);
        }
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
     * <p>
     * The key is lowercased and namespaced under the plugin's ID when the recipe is built.
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
     * Builder for {@link ShapelessCraftingRecipe}s.
     */
    public static final class Builder {
        /**
         * The recipe parts, in the order they were added.
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
         * Whether the recipe also fires in the vanilla Crafter block.
         */
        private boolean crafterSupported = false;

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
         * Set whether the recipe also fires in the vanilla Crafter block.
         * <p>
         * When true, {@link ShapelessCraftingRecipe#register()} additionally
         * registers a Bukkit {@link ShapelessRecipe} at the key
         * {@code <namespace>:<key>_crafter} with {@link RecipeChoice.ExactChoice}
         * ingredients so the Crafter can match it; {@code AutocrafterPatch}
         * will not cancel events fired for these recipes.
         *
         * @param crafterSupported Whether to enable Crafter support.
         * @return The builder.
         */
        public Builder setCrafterSupported(final boolean crafterSupported) {
            this.crafterSupported = crafterSupported;
            return this;
        }

        /**
         * Check if recipe parts are all air.
         * <p>
         * Returns true if no parts have been added at all.
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
         * <p>
         * The built recipe is not registered; call
         * {@link ShapelessCraftingRecipe#register()} on the result.
         *
         * @return The built recipe.
         */
        public ShapelessCraftingRecipe build() {
            return new ShapelessCraftingRecipe(plugin, key.toLowerCase(), recipeParts, output, permission, crafterSupported);
        }
    }

    /**
     * Stateful, single-use test for shapeless recipes.
     * <p>
     * Each call to {@link #matchAndRemove(ItemStack)} consumes at most one remaining part,
     * so a recipe is satisfied when every input has matched and nothing remains.
     */
    public static final class RecipeTest {
        /**
         * The remaining items left to be found.
         */
        private final List<TestableItem> remaining;

        /**
         * Create a new test over a copy of the recipe's parts.
         *
         * @param recipe The recipe to test against.
         */
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
