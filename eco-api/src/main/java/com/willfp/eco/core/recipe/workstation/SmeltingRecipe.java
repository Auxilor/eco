package com.willfp.eco.core.recipe.workstation;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SmeltingRecipe extends WorkstationRecipe {
    private static final int DEFAULT_FURNACE = 200;
    private static final int DEFAULT_BLAST_SMOKER = 100;
    private static final int DEFAULT_CAMPFIRE = 600;

    private final TestableItem input;
    @Nullable private final ItemStack inputDisplay;
    private final SmeltingType smeltingType;
    private final int cookTime;
    private final float experience;

    private SmeltingRecipe(@NotNull NamespacedKey key,
                           @Nullable ItemStack output,
                           @Nullable String permission,
                           @NotNull TestableItem input,
                           @Nullable ItemStack inputDisplay,
                           @NotNull SmeltingType smeltingType,
                           int cookTime,
                           float experience) {
        super(key, output, permission);
        this.input = input;
        this.inputDisplay = inputDisplay;
        this.smeltingType = smeltingType;
        this.cookTime = cookTime;
        this.experience = experience;
    }

    @NotNull
    public TestableItem getInput() {
        return input;
    }

    @Nullable
    public ItemStack getInputDisplay() {
        return inputDisplay;
    }

    @NotNull
    public SmeltingType getSmeltingType() {
        return smeltingType;
    }

    public int getCookTime() {
        return cookTime;
    }

    public float getExperience() {
        return experience;
    }

    private int resolvedCookTime() {
        if (cookTime >= 0) {
            return cookTime;
        }
        return switch (smeltingType) {
            case FURNACE -> DEFAULT_FURNACE;
            case BLAST_FURNACE -> DEFAULT_BLAST_SMOKER;
            case SMOKER -> DEFAULT_BLAST_SMOKER;
            case CAMPFIRE -> DEFAULT_CAMPFIRE;
        };
    }

    @Override
    public void register() {
        WorkstationRecipes.register(this);

        if (getOutput() == null || inputDisplay == null) {
            return;
        }

        RecipeChoice ingredient = new RecipeChoice.ExactChoice(inputDisplay);
        int time = resolvedCookTime();
        NamespacedKey key = getKey();
        ItemStack output = getOutput();

        org.bukkit.inventory.CookingRecipe<?> bukkitRecipe = switch (smeltingType) {
            case FURNACE -> new FurnaceRecipe(key, output, ingredient, experience, time);
            case BLAST_FURNACE -> new BlastingRecipe(key, output, ingredient, experience, time);
            case SMOKER -> new SmokingRecipe(key, output, ingredient, experience, time);
            case CAMPFIRE -> new CampfireRecipe(key, output, ingredient, experience, time);
        };

        Bukkit.addRecipe(bukkitRecipe);
        WorkstationRecipes.trackBukkitKey(key);
    }

    @NotNull
    public static Builder builder(@NotNull NamespacedKey key,
                                  @Nullable ItemStack output,
                                  @NotNull TestableItem input,
                                  @NotNull SmeltingType smeltingType) {
        return new Builder(key, output, input, smeltingType);
    }

    public static final class Builder {
        private final NamespacedKey key;
        private final ItemStack output;
        private final TestableItem input;
        private final SmeltingType smeltingType;
        @Nullable private ItemStack inputDisplay;
        @Nullable private String permission;
        private int cookTime = -1;
        private float experience = 0f;

        private Builder(@NotNull NamespacedKey key,
                        @Nullable ItemStack output,
                        @NotNull TestableItem input,
                        @NotNull SmeltingType smeltingType) {
            this.key = key;
            this.output = output;
            this.input = input;
            this.smeltingType = smeltingType;
        }

        @NotNull
        public Builder cookTime(int cookTime) {
            this.cookTime = cookTime;
            return this;
        }

        @NotNull
        public Builder experience(float experience) {
            this.experience = experience;
            return this;
        }

        @NotNull
        public Builder inputDisplay(@NotNull ItemStack inputDisplay) {
            this.inputDisplay = inputDisplay;
            return this;
        }

        @NotNull
        public Builder permission(@NotNull String permission) {
            this.permission = permission;
            return this;
        }

        @NotNull
        public SmeltingRecipe build() {
            return new SmeltingRecipe(key, output, permission, input, inputDisplay, smeltingType, cookTime, experience);
        }
    }
}
