package com.willfp.eco.core.recipe.workstation;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A custom villager (or wandering trader) trade recipe.
 * <p>
 * Represents a two-input trade: {@link #getInput1() input1} is the primary
 * payment item; {@link #getInput2() input2} is an optional second payment.
 * Trades can be restricted to a specific {@link Villager.Profession profession}
 * and minimum villager level, or flagged for wandering traders instead.
 * <p>
 * The {@link #getChance() chance} field controls the probability that this
 * trade appears in the villager's offer list on each refresh.
 *
 * <p>Use {@link #builder(NamespacedKey, ItemStack, TestableItem)} to construct instances.
 */
public final class VillagerRecipe extends WorkstationRecipe {
    private static final int DEFAULT_MIN_LEVEL = 0;
    private static final double DEFAULT_CHANCE = 1.0;
    private static final boolean DEFAULT_WANDERING_TRADER = false;
    private static final int DEFAULT_VILLAGER_XP = 0;

    private final TestableItem input1;
    @Nullable private final TestableItem input2;
    @Nullable private final ItemStack input1Display;
    @Nullable private final ItemStack input2Display;
    @Nullable private final Villager.Profession profession;
    private final int minLevel;
    private final double chance;
    private final boolean wanderingTrader;
    private final int villagerXp;

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
                           boolean wanderingTrader,
                           int villagerXp) {
        super(key, output, permission);
        this.input1 = input1;
        this.input2 = input2;
        this.input1Display = input1Display;
        this.input2Display = input2Display;
        this.profession = profession;
        this.minLevel = minLevel;
        this.chance = chance;
        this.wanderingTrader = wanderingTrader;
        this.villagerXp = villagerXp;
    }

    /**
     * Get the primary payment item predicate.
     *
     * @return The input1 predicate.
     */
    @NotNull
    public TestableItem getInput1() {
        return input1;
    }

    /**
     * Get the optional secondary payment item predicate.
     *
     * @return The input2 predicate, or null if no second payment is required.
     */
    @Nullable
    public TestableItem getInput2() {
        return input2;
    }

    /**
     * Get the display item for the primary payment slot.
     *
     * @return The input1 display item, or null if not set.
     */
    @Nullable
    public ItemStack getInput1Display() {
        return input1Display;
    }

    /**
     * Get the display item for the secondary payment slot.
     *
     * @return The input2 display item, or null if not set.
     */
    @Nullable
    public ItemStack getInput2Display() {
        return input2Display;
    }

    /**
     * Get the villager profession this trade is restricted to.
     *
     * @return The profession, or null to match any profession.
     */
    @Nullable
    public Villager.Profession getProfession() {
        return profession;
    }

    /**
     * Get the minimum villager level required for this trade to appear.
     *
     * @return Minimum level (1–5). {@code 0} means no restriction.
     */
    public int getMinLevel() {
        return minLevel;
    }

    /**
     * Get the probability that this trade appears in a villager's offer list on refresh.
     *
     * @return Chance in the range {@code [0.0, 1.0]}.
     */
    public double getChance() {
        return chance;
    }

    /**
     * Whether this trade targets wandering traders instead of regular villagers.
     *
     * @return True if this is a wandering trader trade.
     */
    public boolean isWanderingTrader() {
        return wanderingTrader;
    }

    /**
     * Get the XP awarded to the villager when this trade is completed.
     *
     * @return XP amount. {@code 0} means no XP is awarded.
     */
    public int getVillagerXp() {
        return villagerXp;
    }

    @Override
    public void register() {
        WorkstationRecipes.register(this);
    }

    /**
     * Create a new builder for a {@link VillagerRecipe}.
     *
     * @param key    Unique recipe identifier.
     * @param output The item given to the player, or null.
     * @param input1 The primary payment item predicate.
     * @return A new builder.
     */
    @NotNull
    public static Builder builder(@NotNull NamespacedKey key,
                                  @Nullable ItemStack output,
                                  @NotNull TestableItem input1) {
        return new Builder(key, output, input1);
    }

    /**
     * Builder for {@link VillagerRecipe}.
     */
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
        private int villagerXp = DEFAULT_VILLAGER_XP;
        @Nullable private String permission;

        private Builder(@NotNull NamespacedKey key,
                        @Nullable ItemStack output,
                        @NotNull TestableItem input1) {
            this.key = key;
            this.output = output;
            this.input1 = input1;
        }

        /**
         * Set the optional secondary payment item.
         *
         * @param input2 The item predicate, or null for no second payment.
         * @return This builder.
         */
        @NotNull
        public Builder input2(@Nullable TestableItem input2) {
            this.input2 = input2;
            return this;
        }

        /**
         * Set the display item for the primary payment slot.
         *
         * @param input1Display The display item, or null.
         * @return This builder.
         */
        @NotNull
        public Builder input1Display(@Nullable ItemStack input1Display) {
            this.input1Display = input1Display;
            return this;
        }

        /**
         * Set the display item for the secondary payment slot.
         *
         * @param input2Display The display item, or null.
         * @return This builder.
         */
        @NotNull
        public Builder input2Display(@Nullable ItemStack input2Display) {
            this.input2Display = input2Display;
            return this;
        }

        /**
         * Restrict this trade to a specific villager profession.
         *
         * @param profession The profession, or null to match any profession.
         * @return This builder.
         */
        @NotNull
        public Builder profession(@Nullable Villager.Profession profession) {
            this.profession = profession;
            return this;
        }

        /**
         * Set the minimum villager level required for this trade.
         *
         * @param minLevel Minimum level (1–5). Defaults to {@value DEFAULT_MIN_LEVEL} (no restriction).
         * @return This builder.
         */
        @NotNull
        public Builder minLevel(int minLevel) {
            this.minLevel = minLevel;
            return this;
        }

        /**
         * Set the probability this trade appears on offer refresh.
         *
         * @param chance Value in {@code [0.0, 1.0]}. Defaults to {@value DEFAULT_CHANCE}.
         * @return This builder.
         */
        @NotNull
        public Builder chance(double chance) {
            this.chance = chance;
            return this;
        }

        /**
         * Set whether this trade targets wandering traders instead of regular villagers.
         *
         * @param wanderingTrader True for wandering trader trades.
         * @return This builder.
         */
        @NotNull
        public Builder wanderingTrader(boolean wanderingTrader) {
            this.wanderingTrader = wanderingTrader;
            return this;
        }

        /**
         * Set the permission required to use this recipe.
         *
         * @param permission The permission node.
         * @return This builder.
         */
        @NotNull
        public Builder permission(@NotNull String permission) {
            this.permission = permission;
            return this;
        }

        /**
         * Set the XP awarded to the villager on trade completion.
         *
         * @param villagerXp XP amount. Defaults to {@value DEFAULT_VILLAGER_XP}.
         * @return This builder.
         */
        @NotNull
        public Builder villagerXp(int villagerXp) {
            this.villagerXp = villagerXp;
            return this;
        }

        /**
         * Build the {@link VillagerRecipe}.
         *
         * @return The constructed recipe.
         */
        @NotNull
        public VillagerRecipe build() {
            return new VillagerRecipe(key, output, permission, input1, input2,
                    input1Display, input2Display, profession, minLevel, chance, wanderingTrader, villagerXp);
        }
    }
}
