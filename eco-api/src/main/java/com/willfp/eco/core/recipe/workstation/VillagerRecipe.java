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
    /**
     * The minimum villager level used when the builder is not given one.
     */
    private static final int DEFAULT_MIN_LEVEL = 0;

    /**
     * The appearance chance used when the builder is not given one.
     */
    private static final double DEFAULT_CHANCE = 1.0;

    /**
     * The wandering trader flag used when the builder is not given one.
     */
    private static final boolean DEFAULT_WANDERING_TRADER = false;

    /**
     * The villager XP reward used when the builder is not given one.
     */
    private static final int DEFAULT_VILLAGER_XP = 0;

    /**
     * The primary payment item predicate.
     */
    private final TestableItem input1;

    /**
     * The optional secondary payment item predicate.
     */
    @Nullable private final TestableItem input2;

    /**
     * The display item for the primary payment slot.
     */
    @Nullable private final ItemStack input1Display;

    /**
     * The display item for the secondary payment slot.
     */
    @Nullable private final ItemStack input2Display;

    /**
     * The villager profession this trade is restricted to, or null for any.
     */
    @Nullable private final Villager.Profession profession;

    /**
     * The minimum villager level required for this trade to appear.
     */
    private final int minLevel;

    /**
     * The probability this trade appears on offer refresh.
     */
    private final double chance;

    /**
     * Whether this trade targets wandering traders instead of regular villagers.
     */
    private final boolean wanderingTrader;

    /**
     * The XP awarded to the villager when this trade is completed.
     */
    private final int villagerXp;

    /**
     * Create a new villager trade recipe.
     *
     * @param key             Unique recipe identifier.
     * @param output          The item given to the player, or null.
     * @param permission      The permission required to use this recipe, or null.
     * @param input1          The primary payment item predicate.
     * @param input2          The optional secondary payment item predicate, or null.
     * @param input1Display   The display item for the primary payment slot, or null.
     * @param input2Display   The display item for the secondary payment slot, or null.
     * @param profession      The profession restriction, or null for any profession.
     * @param minLevel        The minimum villager level.
     * @param chance          The probability this trade appears on offer refresh.
     * @param wanderingTrader Whether this trade targets wandering traders.
     * @param villagerXp      The XP awarded to the villager on trade completion.
     */
    private VillagerRecipe(@NotNull final NamespacedKey key,
                           @Nullable final ItemStack output,
                           @Nullable final String permission,
                           @NotNull final TestableItem input1,
                           @Nullable final TestableItem input2,
                           @Nullable final ItemStack input1Display,
                           @Nullable final ItemStack input2Display,
                           @Nullable final Villager.Profession profession,
                           final int minLevel,
                           final double chance,
                           final boolean wanderingTrader,
                           final int villagerXp) {
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
     * @return Minimum level (1-5). {@code 0} means no restriction.
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
    public static Builder builder(@NotNull final NamespacedKey key,
                                  @Nullable final ItemStack output,
                                  @NotNull final TestableItem input1) {
        return new Builder(key, output, input1);
    }

    /**
     * Builder for {@link VillagerRecipe}.
     */
    public static final class Builder {
        /**
         * The unique recipe identifier.
         */
        private final NamespacedKey key;

        /**
         * The item given to the player, or null.
         */
        private final ItemStack output;

        /**
         * The primary payment item predicate.
         */
        private final TestableItem input1;

        /**
         * The optional secondary payment item predicate.
         */
        @Nullable private TestableItem input2;

        /**
         * The display item for the primary payment slot.
         */
        @Nullable private ItemStack input1Display;

        /**
         * The display item for the secondary payment slot.
         */
        @Nullable private ItemStack input2Display;

        /**
         * The profession restriction.
         */
        @Nullable private Villager.Profession profession;

        /**
         * The minimum villager level required for this trade.
         */
        private int minLevel = DEFAULT_MIN_LEVEL;

        /**
         * The probability this trade appears on offer refresh.
         */
        private double chance = DEFAULT_CHANCE;

        /**
         * Whether this trade targets wandering traders.
         */
        private boolean wanderingTrader = DEFAULT_WANDERING_TRADER;

        /**
         * The XP awarded to the villager on trade completion.
         */
        private int villagerXp = DEFAULT_VILLAGER_XP;

        /**
         * The permission required to use the recipe.
         */
        @Nullable private String permission;

        /**
         * Create a new builder.
         *
         * @param key    Unique recipe identifier.
         * @param output The item given to the player, or null.
         * @param input1 The primary payment item predicate.
         */
        private Builder(@NotNull final NamespacedKey key,
                        @Nullable final ItemStack output,
                        @NotNull final TestableItem input1) {
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
        public Builder input2(@Nullable final TestableItem input2) {
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
        public Builder input1Display(@Nullable final ItemStack input1Display) {
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
        public Builder input2Display(@Nullable final ItemStack input2Display) {
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
        public Builder profession(@Nullable final Villager.Profession profession) {
            this.profession = profession;
            return this;
        }

        /**
         * Set the minimum villager level required for this trade.
         *
         * @param minLevel Minimum level (1-5). Defaults to {@code 0} (no restriction).
         * @return This builder.
         */
        @NotNull
        public Builder minLevel(final int minLevel) {
            this.minLevel = minLevel;
            return this;
        }

        /**
         * Set the probability this trade appears on offer refresh.
         *
         * @param chance Value in {@code [0.0, 1.0]}. Defaults to {@code 1.0}.
         * @return This builder.
         */
        @NotNull
        public Builder chance(final double chance) {
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
        public Builder wanderingTrader(final boolean wanderingTrader) {
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
        public Builder permission(@NotNull final String permission) {
            this.permission = permission;
            return this;
        }

        /**
         * Set the XP awarded to the villager on trade completion.
         *
         * @param villagerXp XP amount. Defaults to {@code 0}.
         * @return This builder.
         */
        @NotNull
        public Builder villagerXp(final int villagerXp) {
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
