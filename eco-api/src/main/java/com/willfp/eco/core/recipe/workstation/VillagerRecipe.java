package com.willfp.eco.core.recipe.workstation;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class VillagerRecipe extends WorkstationRecipe {
    private static final int DEFAULT_MIN_LEVEL = 0;
    private static final double DEFAULT_CHANCE = 1.0;
    private static final boolean DEFAULT_WANDERING_TRADER = false;

    private final TestableItem input1;
    @Nullable private final TestableItem input2;
    @Nullable private final ItemStack input1Display;
    @Nullable private final ItemStack input2Display;
    @Nullable private final Villager.Profession profession;
    private final int minLevel;
    private final double chance;
    private final boolean wanderingTrader;

    private VillagerRecipe(@NotNull NamespacedKey key,
                           @Nullable ItemStack output,
                           @Nullable String permission,
                           @NotNull TestableItem input1,
                           @Nullable TestableItem input2,
                           @Nullable ItemStack input1Display,
                           @Nullable ItemStack input2Display,
                           @Nullable Villager.Profession profession,
                           int minLevel,
                           double chance,
                           boolean wanderingTrader) {
        super(key, output, permission);
        this.input1 = input1;
        this.input2 = input2;
        this.input1Display = input1Display;
        this.input2Display = input2Display;
        this.profession = profession;
        this.minLevel = minLevel;
        this.chance = chance;
        this.wanderingTrader = wanderingTrader;
    }

    @NotNull
    public TestableItem getInput1() {
        return input1;
    }

    @Nullable
    public TestableItem getInput2() {
        return input2;
    }

    @Nullable
    public ItemStack getInput1Display() {
        return input1Display;
    }

    @Nullable
    public ItemStack getInput2Display() {
        return input2Display;
    }

    @Nullable
    public Villager.Profession getProfession() {
        return profession;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public double getChance() {
        return chance;
    }

    public boolean isWanderingTrader() {
        return wanderingTrader;
    }

    @Override
    public void register() {
        WorkstationRecipes.register(this);
    }

    @NotNull
    public static Builder builder(@NotNull NamespacedKey key,
                                  @Nullable ItemStack output,
                                  @NotNull TestableItem input1) {
        return new Builder(key, output, input1);
    }

    public static final class Builder {
        private final NamespacedKey key;
        private final ItemStack output;
        private final TestableItem input1;
        @Nullable private TestableItem input2;
        @Nullable private ItemStack input1Display;
        @Nullable private ItemStack input2Display;
        @Nullable private Villager.Profession profession;
        private int minLevel = DEFAULT_MIN_LEVEL;
        private double chance = DEFAULT_CHANCE;
        private boolean wanderingTrader = DEFAULT_WANDERING_TRADER;
        @Nullable private String permission;

        private Builder(@NotNull NamespacedKey key,
                        @Nullable ItemStack output,
                        @NotNull TestableItem input1) {
            this.key = key;
            this.output = output;
            this.input1 = input1;
        }

        @NotNull
        public Builder input2(@Nullable TestableItem input2) {
            this.input2 = input2;
            return this;
        }

        @NotNull
        public Builder input1Display(@Nullable ItemStack input1Display) {
            this.input1Display = input1Display;
            return this;
        }

        @NotNull
        public Builder input2Display(@Nullable ItemStack input2Display) {
            this.input2Display = input2Display;
            return this;
        }

        @NotNull
        public Builder profession(@Nullable Villager.Profession profession) {
            this.profession = profession;
            return this;
        }

        @NotNull
        public Builder minLevel(int minLevel) {
            this.minLevel = minLevel;
            return this;
        }

        @NotNull
        public Builder chance(double chance) {
            this.chance = chance;
            return this;
        }

        @NotNull
        public Builder wanderingTrader(boolean wanderingTrader) {
            this.wanderingTrader = wanderingTrader;
            return this;
        }

        @NotNull
        public Builder permission(@NotNull String permission) {
            this.permission = permission;
            return this;
        }

        @NotNull
        public VillagerRecipe build() {
            return new VillagerRecipe(key, output, permission, input1, input2,
                    input1Display, input2Display, profession, minLevel, chance, wanderingTrader);
        }
    }
}
