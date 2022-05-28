package com.willfp.eco.core.gui.slot;

import com.willfp.eco.core.gui.slot.functional.SlotHandler;
import com.willfp.eco.core.gui.slot.functional.SlotModifier;
import com.willfp.eco.core.gui.slot.functional.SlotUpdater;
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
     * Modify the ItemStack.
     *
     * @param modifier The modifier.
     * @return The builder.
     * @deprecated Use {@link SlotBuilder#setUpdater(SlotUpdater)} instead.
     */
    @Deprecated
    default SlotBuilder setModifier(@NotNull SlotModifier modifier) {
        return setUpdater((player, menu, previous) -> {
            modifier.modify(player, menu, previous);
            return previous;
        });
    }

    /**
     * Set the ItemStack updater.
     *
     * @param updater The updater.
     * @return The builder.
     */
    SlotBuilder setUpdater(@NotNull SlotUpdater updater);

    /**
     * Set slot to be a captive slot.
     *
     * @return The builder.
     */
    default SlotBuilder setCaptive() {
        return setCaptive(false);
    }

    /**
     * Set slot to be a captive slot.
     *
     * @param fromEmpty If an item with the same output as the rendered item counts as captive.
     * @return The builder.
     */
    SlotBuilder setCaptive(boolean fromEmpty);

    /**
     * Build the slot.
     *
     * @return The slot.
     */
    Slot build();
}
