package com.willfp.eco.core.recipe.workstation;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GrindstoneRecipe extends WorkstationRecipe {
    private final TestableItem item1;
    @Nullable private final TestableItem item2;

    private GrindstoneRecipe(@NotNull NamespacedKey key,
                             @Nullable ItemStack output,
                             @Nullable String permission,
                             @NotNull TestableItem item1,
                             @Nullable TestableItem item2) {
        super(key, output, permission);
        this.item1 = item1;
        this.item2 = item2;
    }

    @NotNull
    public TestableItem getItem1() {
        return item1;
    }

    @Nullable
    public TestableItem getItem2() {
        return item2;
    }

    @Override
    public void register() {
        WorkstationRecipes.register(this);
    }

    @NotNull
    public static Builder builder(@NotNull NamespacedKey key,
                                  @Nullable ItemStack output,
                                  @NotNull TestableItem item1) {
        return new Builder(key, output, item1);
    }

    public static final class Builder {
        private final NamespacedKey key;
        private final ItemStack output;
        private final TestableItem item1;
        @Nullable private TestableItem item2;
        @Nullable private String permission;

        private Builder(@NotNull NamespacedKey key,
                        @Nullable ItemStack output,
                        @NotNull TestableItem item1) {
            this.key = key;
            this.output = output;
            this.item1 = item1;
        }

        @NotNull
        public Builder item2(@Nullable TestableItem item2) {
            this.item2 = item2;
            return this;
        }

        @NotNull
        public Builder permission(@NotNull String permission) {
            this.permission = permission;
            return this;
        }

        @NotNull
        public GrindstoneRecipe build() {
            return new GrindstoneRecipe(key, output, permission, item1, item2);
        }
    }
}
