package com.willfp.eco.core.gui.slot;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public interface SlotBuilder {
    /**
     * Set click handler.
     *
     * @param action The handler.
     * @return The builder.
     */
    SlotBuilder onLeftClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action);

    /**
     * Set click handler.
     *
     * @param action The handler.
     * @return The builder.
     */
    SlotBuilder onRightClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action);

    /**
     * Set click handler.
     *
     * @param action The handler.
     * @return The builder.
     */
    SlotBuilder onShiftLeftClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action);

    /**
     * Set click handler.
     *
     * @param action The handler.
     * @return The builder.
     */
    SlotBuilder onShiftRightClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action);

    /**
     * Set click handler.
     *
     * @param action The handler.
     * @return The builder.
     */
    SlotBuilder onMiddleClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action);

    /**
     * Build the slot.
     *
     * @return The slot.
     */
    Slot build();
}
