package com.willfp.eco.core.display;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class to manage client-side item display.
 */
public final class Display {
    /**
     * The prefix for client-side lore lines.
     */
    public static final String PREFIX = "§z";

    /**
     * The display handler.
     */
    private static DisplayHandler handler = null;

    /**
     * Display on ItemStacks.
     *
     * @param itemStack The item.
     * @return The ItemStack.
     */
    public static ItemStack display(@NotNull final ItemStack itemStack) {
        return display(itemStack, null);
    }

    /**
     * Display on ItemStacks.
     *
     * @param itemStack The item.
     * @param player    The player.
     * @return The ItemStack.
     */
    public static ItemStack display(@NotNull final ItemStack itemStack,
                                    @Nullable final Player player) {
        return handler.display(itemStack, player);
    }

    /**
     * Display on ItemStacks and then finalize.
     *
     * @param itemStack The item.
     * @return The ItemStack.
     */
    public static ItemStack displayAndFinalize(@NotNull final ItemStack itemStack) {
        return finalize(display(itemStack, null));
    }

    /**
     * Display on ItemStacks and then finalize.
     *
     * @param itemStack The item.
     * @param player    The player.
     * @return The ItemStack.
     */
    public static ItemStack displayAndFinalize(@NotNull final ItemStack itemStack,
                                               @Nullable final Player player) {
        return finalize(display(itemStack, player));
    }

    /**
     * Revert on ItemStacks.
     *
     * @param itemStack The item.
     * @return The ItemStack.
     */
    public static ItemStack revert(@NotNull final ItemStack itemStack) {
        return handler.revert(itemStack);
    }

    /**
     * Finalize an ItemStacks.
     *
     * @param itemStack The item.
     * @return The ItemStack.
     */
    public static ItemStack finalize(@NotNull final ItemStack itemStack) {
        return handler.finalize(itemStack);
    }

    /**
     * Unfinalize an ItemStacks.
     *
     * @param itemStack The item.
     * @return The ItemStack.
     */
    public static ItemStack unfinalize(@NotNull final ItemStack itemStack) {
        return handler.unfinalize(itemStack);
    }

    /**
     * If an item is finalized.
     *
     * @param itemStack The item.
     * @return If finalized.
     */
    public static boolean isFinalized(@NotNull final ItemStack itemStack) {
        return handler.isFinalized(itemStack);
    }

    /**
     * Register a new display module.
     *
     * @param module The module.
     */
    public static void registerDisplayModule(@NotNull final DisplayModule module) {
        handler.registerDisplayModule(module);
    }

    /**
     * Set the display handler.
     * <p>
     * Internal API component, you will cause bugs if you create your own handler.
     *
     * @param handler The handler.
     */
    @ApiStatus.Internal
    public static void setHandler(@NotNull final DisplayHandler handler) {
        if (Display.handler != null) {
            throw new IllegalStateException("Display already initialized!");
        }

        Display.handler = handler;
    }

    private Display() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
