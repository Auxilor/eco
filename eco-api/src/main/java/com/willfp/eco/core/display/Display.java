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
     * Initialize the display system.
     *
     * @param handler The handler.
     */
    @ApiStatus.Internal
    public static void init(@NotNull final DisplayHandler handler) {
        if (Display.handler != null) {
            throw new IllegalArgumentException("Already Initialized!");
        }
        Display.handler = handler;
    }

    /**
     * Extremely janky method - also internal, so don't use it. <b>This method is
     * NOT part of the API and may be removed at any time!</b>
     * <p>
     * This calls a display module with the specified parameters, now
     * you might ask why I need a static java method when the DisplayHandler
     * implementation could just call it itself? Well, kotlin doesn't really
     * like dealing with vararg ambiguity, and so while kotlin can't figure out
     * what is and isn't a vararg when I call display with a player, java can.
     * <p>
     * Because of this, I need to have this part of the code in java.
     *
     * <b>Don't call this method as part of your plugins!</b>
     * <p>
     * No, seriously - don't. This skips a bunch of checks and you'll almost
     * definitely break something.
     *
     * @param module    The display module.
     * @param itemStack The ItemStack.
     * @param player    The player.
     * @param args      The args.
     */
    @ApiStatus.Internal
    public static void callDisplayModule(@NotNull final DisplayModule module,
                                         @NotNull final ItemStack itemStack,
                                         @Nullable final Player player,
                                         @NotNull final Object... args) {
        module.display(itemStack, args);
        if (player != null) {
            module.display(itemStack, player, args);
        }
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
