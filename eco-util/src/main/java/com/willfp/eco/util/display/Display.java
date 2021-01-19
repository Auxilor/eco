package com.willfp.eco.util.display;

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@UtilityClass
public class Display {
    /**
     * Registered display functions.
     */
    private static final List<List<Function<ItemStack, ItemStack>>> DISPLAY_FUNCTIONS = new ArrayList<>();

    /**
     * Registered revert functions.
     */
    private static final List<Function<ItemStack, ItemStack>> REVERT_FUNCTIONS = new ArrayList<>();

    /**
     * Registered finalize functions.
     */
    public static final List<Function<ItemStack, ItemStack>> FINALIZE_FUNCTIONS = new ArrayList<>();


    /**
     * Registered finalize test functions.
     */
    public static final List<Predicate<ItemStack>> FINALIZE_TEST_FUNCTIONS = new ArrayList<>();

    /**
     * Register display module.
     *
     * @param module The module.
     */
    public void registerDisplayModule(@NotNull final DisplayModule module) {
        int priority = module.getPriority();
        Function<ItemStack, ItemStack> function = module.getFunction();

        List<Function<ItemStack, ItemStack>> functions = DISPLAY_FUNCTIONS.get(priority);
        if (functions == null) {
            functions = new ArrayList<>();
        }

        functions.add(function);

        DISPLAY_FUNCTIONS.set(priority, functions);
    }

    /**
     * Register revert function.
     *
     * @param function The function.
     */
    public void registerRevertModule(@NotNull final Function<ItemStack, ItemStack> function) {
        REVERT_FUNCTIONS.add(function);
    }

    /**
     * Register finalize function.
     *
     * @param function The function.
     */
    public void registerFinalizeModule(@NotNull final Function<ItemStack, ItemStack> function) {
        FINALIZE_FUNCTIONS.add(function);
    }

    /**
     * Register finalize test function.
     *
     * @param function The function.
     */
    public void registerFinalizeTestModule(@NotNull final Predicate<ItemStack> function) {
        FINALIZE_TEST_FUNCTIONS.add(function);
    }

    /**
     * Display on ItemStacks.
     *
     * @param itemStack The item.
     */
    public ItemStack display(@NotNull final ItemStack itemStack) {
        for (List<Function<ItemStack, ItemStack>> displayFunctions : DISPLAY_FUNCTIONS) {
            if (displayFunctions == null) {
                continue;
            }

            for (Function<ItemStack, ItemStack> displayFunction : displayFunctions) {
                displayFunction.apply(itemStack);
            }
        }

        return itemStack;
    }

    /**
     * Display on ItemStacks and then finalize.
     *
     * @param itemStack The item.
     */
    public ItemStack displayAndFinalize(@NotNull final ItemStack itemStack) {
        return finalize(display(itemStack));
    }

    /**
     * Revert on ItemStacks.
     *
     * @param itemStack The item.
     */
    public ItemStack revert(@NotNull final ItemStack itemStack) {
        for (Function<ItemStack, ItemStack> displayFunction : REVERT_FUNCTIONS) {
            displayFunction.apply(itemStack);
        }
        return itemStack;
    }

    /**
     * Finalize an ItemStacks.
     *
     * @param itemStack The item.
     */
    public ItemStack finalize(@NotNull final ItemStack itemStack) {
        for (Function<ItemStack, ItemStack> function : FINALIZE_FUNCTIONS) {
            function.apply(itemStack);
        }
        return itemStack;
    }

    /**
     * Finalize an ItemStacks.
     *
     * @param itemStack The item.
     */
    public boolean isFinalized(@NotNull final ItemStack itemStack) {
        for (Predicate<ItemStack> function : FINALIZE_TEST_FUNCTIONS) {
            if (function.test(itemStack)) {
                return true;
            }
        }

        return false;
    }
}
