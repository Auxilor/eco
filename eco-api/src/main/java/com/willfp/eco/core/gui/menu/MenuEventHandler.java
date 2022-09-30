package com.willfp.eco.core.gui.menu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles menu events.
 */
public abstract class MenuEventHandler<T extends MenuEvent> {
    /**
     * The class of the event.
     */
    private final Class<T> eventClass;

    /**
     * Create a new menu event handler.
     *
     * @param eventClass The class of event to handle.
     */
    protected MenuEventHandler(@NotNull final Class<T> eventClass) {
        this.eventClass = eventClass;
    }


    /**
     * Performs this operation on the given arguments.
     *
     * @param player The player.
     * @param menu   The menu.
     * @param event  The event.
     */
    public abstract void handle(@NotNull Player player,
                                @NotNull Menu menu,
                                @NotNull T event);

    /**
     * Get if this handler can handle a certain event.
     *
     * @param menuEvent The event
     * @return If the event can be handled.
     */
    public boolean canHandleEvent(@NotNull final MenuEvent menuEvent) {
        return eventClass.isAssignableFrom(menuEvent.getClass());
    }
}
