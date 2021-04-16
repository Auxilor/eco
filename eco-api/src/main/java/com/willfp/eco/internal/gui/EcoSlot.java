package com.willfp.eco.internal.gui;

import com.willfp.eco.core.gui.slot.Slot;
import lombok.Getter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class EcoSlot implements Slot {
    @Getter
    private final ItemStack itemStack;

    private final BiConsumer<InventoryClickEvent, Slot> onLeftClick;

    private final BiConsumer<InventoryClickEvent, Slot> onRightClick;

    private final BiConsumer<InventoryClickEvent, Slot> onShiftLeftClick;

    private final BiConsumer<InventoryClickEvent, Slot> onShiftRightClick;

    private final BiConsumer<InventoryClickEvent, Slot> onMiddleClick;

    public EcoSlot(@NotNull final ItemStack itemStack,
                   @Nullable final BiConsumer<InventoryClickEvent, Slot> onLeftClick,
                   @Nullable final BiConsumer<InventoryClickEvent, Slot> onRightClick,
                   @Nullable final BiConsumer<InventoryClickEvent, Slot> onShiftLeftClick,
                   @Nullable final BiConsumer<InventoryClickEvent, Slot> onShiftRightClick,
                   @Nullable final BiConsumer<InventoryClickEvent, Slot> onMiddleClick) {
        this.itemStack = itemStack;
        this.onLeftClick = onLeftClick == null ? ((event, slot) -> { }) : onLeftClick;
        this.onRightClick = onRightClick == null ? ((event, slot) -> { }) : onRightClick;
        this.onShiftLeftClick = onShiftLeftClick == null ? ((event, slot) -> { }) : onShiftLeftClick;
        this.onShiftRightClick = onShiftRightClick == null ? ((event, slot) -> { }) : onShiftRightClick;
        this.onMiddleClick = onMiddleClick == null ? ((event, slot) -> { }) : onMiddleClick;
    }

    public void handleInventoryClick(@NotNull final InventoryClickEvent event) {
        switch (event.getClick()) {
            case LEFT:
                this.onLeftClick.accept(event, this);
                break;
            case RIGHT:
                this.onRightClick.accept(event, this);
                break;
            case SHIFT_LEFT:
                this.onShiftLeftClick.accept(event, this);
                break;
            case SHIFT_RIGHT:
                this.onShiftRightClick.accept(event, this);
                break;
            case MIDDLE:
                this.onMiddleClick.accept(event, this);
                break;
            default:
                break;
        }
    }
}
