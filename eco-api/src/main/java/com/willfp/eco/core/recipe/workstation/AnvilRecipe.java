package com.willfp.eco.core.recipe.workstation;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AnvilRecipe extends WorkstationRecipe {
    private static final int DEFAULT_REPAIR_COST = 1;

    private final TestableItem base;
    @Nullable private final TestableItem material;
    @Nullable private final String resultName;
    private final int repairCost;

    private AnvilRecipe(@NotNull NamespacedKey key,
                        @Nullable ItemStack output,
                        @Nullable String permission,
                        @NotNull TestableItem base,
                        @Nullable TestableItem material,
                        @Nullable String resultName,
                        int repairCost) {
        super(key, output, permission);
        this.base = base;
        this.material = material;
        this.resultName = resultName;
        this.repairCost = repairCost;
    }

    @NotNull
    public TestableItem getBase() {
        return base;
    }

    @Nullable
    public TestableItem getMaterial() {
        return material;
    }

    @Nullable
    public String getResultName() {
        return resultName;
    }

    public int getRepairCost() {
        return repairCost;
    }

    @Override
    public void register() {
        WorkstationRecipes.register(this);
    }

    @NotNull
    public static Builder builder(@NotNull NamespacedKey key,
                                  @Nullable ItemStack output,
                                  @NotNull TestableItem base) {
        return new Builder(key, output, base);
    }

    public static final class Builder {
        private final NamespacedKey key;
        private final ItemStack output;
        private final TestableItem base;
        @Nullable private TestableItem material;
        @Nullable private String resultName;
        private int repairCost = DEFAULT_REPAIR_COST;
        @Nullable private String permission;

        private Builder(@NotNull NamespacedKey key,
                        @Nullable ItemStack output,
                        @NotNull TestableItem base) {
            this.key = key;
            this.output = output;
            this.base = base;
        }

        @NotNull
        public Builder material(@Nullable TestableItem material) {
            this.material = material;
            return this;
        }

        @NotNull
        public Builder resultName(@Nullable String resultName) {
            this.resultName = resultName;
            return this;
        }

        @NotNull
        public Builder repairCost(int repairCost) {
            this.repairCost = repairCost;
            return this;
        }

        @NotNull
        public Builder permission(@NotNull String permission) {
            this.permission = permission;
            return this;
        }

        @NotNull
        public AnvilRecipe build() {
            return new AnvilRecipe(key, output, permission, base, material, resultName, repairCost);
        }
    }
}
