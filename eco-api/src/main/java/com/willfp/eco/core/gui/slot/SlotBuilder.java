package com.willfp.eco.core.gui.slot;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * Builder to create slots.
 */
public interface SlotBuilder {
    /**
     * Set click handler.
     *
     * @param action The handler.
     * @return The builder.
     */
    @Deprecated
    default SlotBuilder onLeftClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action) {
        return onLeftClick((event, slot, menu) -> action.accept(event, slot));
    }

    /**
     * Set click handler.
     *
     * @param handler The handler.
     * @return The builder.
     */
    SlotBuilder onLeftClick(@NotNull SlotHandler handler);

    /**
     * Set click handler.
     *
     * @param action The handler.
     * @return The builder.
     */
    @Deprecated
    default SlotBuilder onRightClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action) {
        return onRightClick((event, slot, menu) -> action.accept(event, slot));
    }

    /**
     * Set click handler.
     *
     * @param handler The handler.
     * @return The builder.
     */
    SlotBuilder onRightClick(@NotNull SlotHandler handler);

    /**
     * Set click handler.
     *
     * @param action The handler.
     * @return The builder.
     */
    @Deprecated
    default SlotBuilder onShiftLeftClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action) {
        return onShiftLeftClick((event, slot, menu) -> action.accept(event, slot));
    }

    /**
     * Set click handler.
     *
     * @param handler The handler.
     * @return The builder.
     */
    SlotBuilder onShiftLeftClick(@NotNull SlotHandler handler);

    /**
     * Set click handler.
     *
     * @param action The handler.
     * @return The builder.
     */
    @Deprecated
    default SlotBuilder onShiftRightClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action) {
        return onShiftRightClick((event, slot, menu) -> action.accept(event, slot));
    }

    /**
     * Set click handler.
     *
     * @param handler The handler.
     * @return The builder.
     */
    SlotBuilder onShiftRightClick(@NotNull SlotHandler handler);

    /**
     * Set click handler.
     *
     * @param action The handler.
     * @return The builder.
     */
    @Deprecated
    default SlotBuilder onMiddleClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action) {
        return onMiddleClick((event, slot, menu) -> action.accept(event, slot));
    }

    /**
     * Set click handler.
     *
     * @param handler The handler.
     * @return The builder.
     */
    SlotBuilder onMiddleClick(@NotNull SlotHandler handler);

    /**
     * Set slot to be a captive slot.
     *
     * @return The builder.
     */
    SlotBuilder setCaptive();

    /**
     * Build the slot.
     *
     * @return The slot.
     */
    Slot build();
}
