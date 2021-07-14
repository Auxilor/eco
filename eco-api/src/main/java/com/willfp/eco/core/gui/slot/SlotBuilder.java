package com.willfp.eco.core.gui.slot;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public interface SlotBuilder {
    SlotBuilder onLeftClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action);

    SlotBuilder onRightClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action);

    SlotBuilder onShiftLeftClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action);

    SlotBuilder onShiftRightClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action);

    SlotBuilder onMiddleClick(@NotNull BiConsumer<InventoryClickEvent, Slot> action);

    Slot build();
}
