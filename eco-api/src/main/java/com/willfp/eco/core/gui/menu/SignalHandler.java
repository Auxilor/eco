package com.willfp.eco.core.gui.menu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles signals sent to menus.
 */
public abstract class SignalHandler<T extends Signal> {
    /**
     * The class of signal.
     */
    private final Class<T> signalClass;

    /**
     * Create a new signal handler.
     *
     * @param signalClass The class of signal to handle.
     */
    protected SignalHandler(@NotNull final Class<T> signalClass) {
        this.signalClass = signalClass;
    }


    /**
     * Performs this operation on the given arguments.
     *
     * @param player The player.
     * @param menu   The menu.
     * @param signal The signal.
     */
    public abstract void handle(@NotNull Player player,
                                @NotNull Menu menu,
                                @NotNull T signal);

    /**
     * Get if this handler can handle a certain signal.
     *
     * @param signal The signal
     * @return If the signal can be handled.
     */
    public boolean canHandleSignal(@NotNull final Signal signal) {
        return signalClass.isAssignableFrom(signal.getClass());
    }
}
