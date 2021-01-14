package com.willfp.eco.util.recipes.parts;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ComplexRecipePart implements RecipePart {
    /**
     * The test for itemstacks to pass.
     */
    @Getter
    private final Predicate<ItemStack> predicate;

    /**
     * Displayed itemstack: what the user should see.
     */
    @Getter
    private final ItemStack displayed;

    /**
     * Create a new complex recipe part.
     * @param predicate The test.
     * @param displayed The example itemstack.
     */
    public ComplexRecipePart(@NotNull final Predicate<ItemStack> predicate,
                             @NotNull final ItemStack displayed) {
        this.predicate = predicate;
        this.displayed = displayed;
    }

    @Override
    public boolean matches(@Nullable final ItemStack itemStack) {
        return predicate.test(itemStack);
    }

    @Override
    public ItemStack getDisplayed() {
        return displayed;
    }
}
