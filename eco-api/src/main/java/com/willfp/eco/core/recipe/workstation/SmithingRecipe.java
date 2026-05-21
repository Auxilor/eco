package com.willfp.eco.core.recipe.workstation;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SmithingRecipe extends WorkstationRecipe {
    private final TestableItem template;
    @Nullable private final ItemStack templateDisplay;
    private final TestableItem base;
    @Nullable private final ItemStack baseDisplay;
    private final TestableItem addition;
    @Nullable private final ItemStack additionDisplay;

    private SmithingRecipe(@NotNull NamespacedKey key,
                           @Nullable ItemStack output,
                           @Nullable String permission,
                           @NotNull TestableItem template,
                           @Nullable ItemStack templateDisplay,
                           @NotNull TestableItem base,
                           @Nullable ItemStack baseDisplay,
                           @NotNull TestableItem addition,
                           @Nullable ItemStack additionDisplay) {
        super(key, output, permission);
        this.template = template;
        this.templateDisplay = templateDisplay;
        this.base = base;
        this.baseDisplay = baseDisplay;
        this.addition = addition;
        this.additionDisplay = additionDisplay;
    }

    @NotNull
    public TestableItem getTemplate() {
        return template;
    }

    @Nullable
    public ItemStack getTemplateDisplay() {
        return templateDisplay;
    }

    @NotNull
    public TestableItem getBase() {
        return base;
    }

    @Nullable
    public ItemStack getBaseDisplay() {
        return baseDisplay;
    }

    @NotNull
    public TestableItem getAddition() {
        return addition;
    }

    @Nullable
    public ItemStack getAdditionDisplay() {
        return additionDisplay;
    }

    @Override
    public void register() {
        WorkstationRecipes.register(this);

        if (getOutput() == null || templateDisplay == null || baseDisplay == null || additionDisplay == null) {
            return;
        }

        NamespacedKey key = getKey();
        SmithingTransformRecipe bukkitRecipe = new SmithingTransformRecipe(
                key,
                getOutput(),
                new RecipeChoice.ExactChoice(templateDisplay),
                new RecipeChoice.ExactChoice(baseDisplay),
                new RecipeChoice.ExactChoice(additionDisplay)
        );

        Bukkit.addRecipe(bukkitRecipe);
        WorkstationRecipes.trackBukkitKey(key);
    }

    @NotNull
    public static Builder builder(@NotNull NamespacedKey key, @Nullable ItemStack output) {
        return new Builder(key, output);
    }

    public static final class Builder {
        private final NamespacedKey key;
        private final ItemStack output;
        @Nullable private String permission;
        private TestableItem template;
        @Nullable private ItemStack templateDisplay;
        private TestableItem base;
        @Nullable private ItemStack baseDisplay;
        private TestableItem addition;
        @Nullable private ItemStack additionDisplay;

        private Builder(@NotNull NamespacedKey key, @Nullable ItemStack output) {
            this.key = key;
            this.output = output;
        }

        @NotNull
        public Builder template(@NotNull TestableItem template, @Nullable ItemStack templateDisplay) {
            this.template = template;
            this.templateDisplay = templateDisplay;
            return this;
        }

        @NotNull
        public Builder base(@NotNull TestableItem base, @Nullable ItemStack baseDisplay) {
            this.base = base;
            this.baseDisplay = baseDisplay;
            return this;
        }

        @NotNull
        public Builder addition(@NotNull TestableItem addition, @Nullable ItemStack additionDisplay) {
            this.addition = addition;
            this.additionDisplay = additionDisplay;
            return this;
        }

        @NotNull
        public Builder permission(@NotNull String permission) {
            this.permission = permission;
            return this;
        }

        @NotNull
        public SmithingRecipe build() {
            if (template == null || base == null || addition == null) {
                throw new IllegalStateException("SmithingRecipe requires template, base, and addition to be set");
            }
            return new SmithingRecipe(key, output, permission, template, templateDisplay, base, baseDisplay, addition, additionDisplay);
        }
    }
}
