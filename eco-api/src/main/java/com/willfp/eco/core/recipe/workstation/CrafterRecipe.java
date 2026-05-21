package com.willfp.eco.core.recipe.workstation;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class CrafterRecipe extends WorkstationRecipe {
    private final List<TestableItem> parts;
    private final List<ItemStack> partDisplays;
    private final boolean shapeless;

    private CrafterRecipe(@NotNull NamespacedKey key,
                          @Nullable ItemStack output,
                          @Nullable String permission,
                          @NotNull List<TestableItem> parts,
                          @NotNull List<ItemStack> partDisplays,
                          boolean shapeless) {
        super(key, output, permission);
        this.parts = parts;
        this.partDisplays = partDisplays;
        this.shapeless = shapeless;
    }

    @NotNull
    public List<TestableItem> getParts() {
        return parts;
    }

    @NotNull
    public List<ItemStack> getPartDisplays() {
        return partDisplays;
    }

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

        ShapedRecipe bukkit = new ShapedRecipe(crafterKey, getOutput());

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
        bukkit.shape(row0, row1, row2);

        for (int i = 0; i < 9; i++) {
            if (parts.get(i) != null && partDisplays.get(i) != null) {
                char c = slotChars[i];
                bukkit.setIngredient(c, new RecipeChoice.ExactChoice(partDisplays.get(i).clone()));
            }
        }

        Bukkit.addRecipe(bukkit);
        WorkstationRecipes.trackBukkitKey(crafterKey);
    }

    @NotNull
    public static Builder builder(@NotNull NamespacedKey key,
                                  @Nullable ItemStack output) {
        return new Builder(key, output);
    }

    public static final class Builder {
        private final NamespacedKey key;
        private final ItemStack output;
        @Nullable private String permission;
        private List<TestableItem> parts = new ArrayList<>(java.util.Arrays.asList(new TestableItem[9]));
        private List<ItemStack> partDisplays = new ArrayList<>(java.util.Arrays.asList(new ItemStack[9]));
        private boolean shapeless = false;

        private Builder(@NotNull NamespacedKey key,
                        @Nullable ItemStack output) {
            this.key = key;
            this.output = output;
        }

        @NotNull
        public Builder parts(@NotNull List<TestableItem> parts, @NotNull List<ItemStack> displays) {
            this.parts = parts;
            this.partDisplays = displays;
            return this;
        }

        @NotNull
        public Builder shapeless(boolean shapeless) {
            this.shapeless = shapeless;
            return this;
        }

        @NotNull
        public Builder permission(@NotNull String permission) {
            this.permission = permission;
            return this;
        }

        @NotNull
        public CrafterRecipe build() {
            return new CrafterRecipe(key, output, permission, parts, partDisplays, shapeless);
        }
    }
}
