package com.willfp.eco.core.gui.slot;

import com.willfp.eco.core.gui.slot.functional.SlotHandler;
import com.willfp.eco.core.gui.slot.functional.SlotModifier;
import com.willfp.eco.core.gui.slot.functional.SlotUpdater;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Builder to create slots.
 */
public interface SlotBuilder {
    /**
     * Set click handler.
     *
     * @param type    The click type.
     * @param handler The handler.
     * @return The builder.
     */
    SlotBuilder onClick(@NotNull ClickType type,
                        @NotNull SlotHandler handler);

    /**
     * Set click handler.
     *
     * @param type   The click type.
     * @param action The handler.
     * @return The builder.
     */
    default SlotBuilder onClick(@NotNull final ClickType type,
                                @NotNull final BiConsumer<InventoryClickEvent, Slot> action) {
        return onClick(type, (event, slot, menu) -> action.accept(event, slot));
    }

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
    default SlotBuilder onLeftClick(@NotNull final SlotHandler handler) {
        return onClick(ClickType.LEFT, handler);
    }

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
    default SlotBuilder onRightClick(@NotNull final SlotHandler handler) {
        return onClick(ClickType.RIGHT, handler);
    }

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
    default SlotBuilder onShiftLeftClick(@NotNull final SlotHandler handler) {
        return onClick(ClickType.SHIFT_LEFT, handler);
    }

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
    default SlotBuilder onShiftRightClick(@NotNull final SlotHandler handler) {
        return onClick(ClickType.SHIFT_RIGHT, handler);
    }

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
    default SlotBuilder onMiddleClick(@NotNull final SlotHandler handler) {
        return onClick(ClickType.MIDDLE, handler);
    }

    /**
     * Prevent captive for players that match a predicate.
     *
     * @param predicate The predicate. Returns true when the slot should not be captive.
     * @return The builder.
     */
    SlotBuilder notCaptiveFor(@NotNull Predicate<Player> predicate);

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
}
