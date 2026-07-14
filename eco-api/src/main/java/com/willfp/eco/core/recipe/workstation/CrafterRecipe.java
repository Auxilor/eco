package com.willfp.eco.core.recipe.workstation;

import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.recipe.Recipes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A custom crafter (auto-crafter) recipe.
 * <p>
 * Wraps a 3x3 grid of up to nine ingredient slots. Each slot is defined by a
 * {@link com.willfp.eco.core.items.TestableItem} predicate (for match logic) and
 * a display {@link ItemStack} (registered with Bukkit as an
 * {@link RecipeChoice.ExactChoice} so the crafter UI shows the correct icon).
 * <p>
 * When {@link #register()} is called a Bukkit {@link ShapedRecipe} is also
 * registered under the key {@code <namespace>:<key>_crafter} so the vanilla
 * crafter block can preview and execute the recipe.
 *
 * <p>Use {@link #builder(NamespacedKey, ItemStack)} to construct instances.
 */
public final class CrafterRecipe extends WorkstationRecipe {
    private final List<TestableItem> parts;
    private final List<ItemStack> partDisplays;
    private final boolean shapeless;

    private CrafterRecipe(@NotNull final NamespacedKey key,
                          @Nullable final ItemStack output,
                          @Nullable final String permission,
                          @NotNull final List<TestableItem> parts,
                          @NotNull final List<ItemStack> partDisplays,
                          final boolean shapeless) {
        super(key, output, permission);
        this.parts = parts;
        this.partDisplays = partDisplays;
        this.shapeless = shapeless;
    }

    /**
     * Get the ingredient predicates for all nine crafter slots (indices 0-8,
     * left-to-right, top-to-bottom). Null entries represent empty slots.
     *
     * @return The parts list.
     */
    @NotNull
    public List<TestableItem> getParts() {
        return parts;
    }

    /**
     * Get the display items for all nine crafter slots.
     * <p>
     * These are registered with Bukkit as {@link RecipeChoice.ExactChoice}s so
     * the crafter block shows the correct ingredient icons.
     *
     * @return The part display items list.
     */
    @NotNull
    public List<ItemStack> getPartDisplays() {
        return partDisplays;
    }

    /**
     * Whether this recipe is shapeless.
     * <p>
     * Shapeless recipes match regardless of the order ingredients are placed.
     *
     * @return True if shapeless.
     */
    public boolean isShapeless() {
        return shapeless;
    }

    @Override
    public void register() {
        WorkstationRecipes.register(this);

        if (getOutput() == null) {
            return;
        }

        NamespacedKey key = getKey();
        NamespacedKey crafterKey = new NamespacedKey(key.getNamespace(), key.getKey() + "_crafter");

        ShapedRecipe shapedRecipe = new ShapedRecipe(crafterKey, getOutput());

        // Map non-null slots to chars A-I
        char[] slotChars = new char[9];
        for (int i = 0; i < 9; i++) {
            if (parts.get(i) != null && partDisplays.get(i) != null) {
                slotChars[i] = (char) ('A' + i);
            } else {
                slotChars[i] = ' ';
            }
        }

        // Build 3 rows of 3 chars each
        String row0 = "" + slotChars[0] + slotChars[1] + slotChars[2];
        String row1 = "" + slotChars[3] + slotChars[4] + slotChars[5];
        String row2 = "" + slotChars[6] + slotChars[7] + slotChars[8];
        shapedRecipe.shape(row0, row1, row2);

        for (int i = 0; i < 9; i++) {
            if (parts.get(i) != null && partDisplays.get(i) != null) {
                char slotChar = slotChars[i];
                shapedRecipe.setIngredient(slotChar, new RecipeChoice.ExactChoice(partDisplays.get(i).clone()));
            }
        }

        Recipes.scheduleBukkitRecipeRegistration(shapedRecipe);
        WorkstationRecipes.trackBukkitKey(crafterKey);
    }

    /**
     * Create a new builder for a {@link CrafterRecipe}.
     *
     * @param key    Unique recipe identifier.
     * @param output The item produced, or null.
     * @return A new builder.
     */
    @NotNull
    public static Builder builder(@NotNull final NamespacedKey key,
                                  @Nullable final ItemStack output) {
        return new Builder(key, output);
    }

    /**
     * Builder for {@link CrafterRecipe}.
     */
    public static final class Builder {
        private final NamespacedKey key;
        private final ItemStack output;
        @Nullable private String permission;
        private List<TestableItem> parts = new ArrayList<>(Arrays.asList(new TestableItem[9]));
        private List<ItemStack> partDisplays = new ArrayList<>(Arrays.asList(new ItemStack[9]));
        private boolean shapeless = false;

        private Builder(@NotNull final NamespacedKey key,
                        @Nullable final ItemStack output) {
            this.key = key;
            this.output = output;
        }

        /**
         * Set the ingredient predicates and their corresponding display items.
         * <p>
         * Both lists must have exactly nine elements (null for empty slots).
         *
         * @param parts    The ingredient predicates.
         * @param displays The display items registered with Bukkit.
         * @return This builder.
         */
        @NotNull
        public Builder parts(@NotNull final List<TestableItem> parts, @NotNull final List<ItemStack> displays) {
            this.parts = parts;
            this.partDisplays = displays;
            return this;
        }

        /**
         * Set whether this recipe is shapeless.
         *
         * @param shapeless True to match ingredients in any order.
         * @return This builder.
         */
        @NotNull
        public Builder shapeless(final boolean shapeless) {
            this.shapeless = shapeless;
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
         * Build the {@link CrafterRecipe}.
         *
         * @return The constructed recipe.
         */
        @NotNull
        public CrafterRecipe build() {
            return new CrafterRecipe(key, output, permission, parts, partDisplays, shapeless);
        }
    }
}
