package com.willfp.eco.core.recipe.workstation;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BrewingRecipe extends WorkstationRecipe {
    private static final int DEFAULT_BREW_TIME = 400;

    private final TestableItem base;
    private final TestableItem ingredient;
    private final int brewTime;

    private BrewingRecipe(@NotNull NamespacedKey key,
                          @Nullable ItemStack output,
                          @Nullable String permission,
                          @NotNull TestableItem base,
                          @NotNull TestableItem ingredient,
                          int brewTime) {
        super(key, output, permission);
        this.base = base;
        this.ingredient = ingredient;
        this.brewTime = brewTime;
    }

    @NotNull
    public TestableItem getBase() {
        return base;
    }

    @NotNull
    public TestableItem getIngredient() {
        return ingredient;
    }

    public int getBrewTime() {
        return brewTime;
    }

    @Override
    public void register() {
        WorkstationRecipes.register(this);
    }

    @NotNull
    public static Builder builder(@NotNull NamespacedKey key,
                                  @Nullable ItemStack output,
                                  @NotNull TestableItem base,
                                  @NotNull TestableItem ingredient) {
        return new Builder(key, output, base, ingredient);
    }

    public static final class Builder {
        private final NamespacedKey key;
        private final ItemStack output;
        private final TestableItem base;
        private final TestableItem ingredient;
        @Nullable private String permission;
        private int brewTime = DEFAULT_BREW_TIME;

        private Builder(@NotNull NamespacedKey key,
                        @Nullable ItemStack output,
                        @NotNull TestableItem base,
                        @NotNull TestableItem ingredient) {
            this.key = key;
            this.output = output;
            this.base = base;
            this.ingredient = ingredient;
        }

        @NotNull
        public Builder brewTime(int brewTime) {
            this.brewTime = brewTime;
            return this;
        }

        @NotNull
        public Builder permission(@NotNull String permission) {
            this.permission = permission;
            return this;
        }

        @NotNull
        public BrewingRecipe build() {
            return new BrewingRecipe(key, output, permission, base, ingredient, brewTime);
        }
    }
}
