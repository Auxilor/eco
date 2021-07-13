package com.willfp.eco.core.gui.slot;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

class EcoSlot implements Slot {
    /**
     * The item provider.
     */
    @Getter
    private final Function<Player, ItemStack> provider;

    /**
     * Left click handler.
     */
    private final BiConsumer<InventoryClickEvent, Slot> onLeftClick;

    /**
     * Right click handler.
     */
    private final BiConsumer<InventoryClickEvent, Slot> onRightClick;

    /**
     * Shift-Left-Click handler.
     */
    private final BiConsumer<InventoryClickEvent, Slot> onShiftLeftClick;

    /**
     * Shift-Right-Click handler.
     */
    private final BiConsumer<InventoryClickEvent, Slot> onShiftRightClick;

    /**
     * Shift-Middle-Click handler.
     */
    private final BiConsumer<InventoryClickEvent, Slot> onMiddleClick;

    EcoSlot(@NotNull final Function<Player, ItemStack> provider,
            @Nullable final BiConsumer<InventoryClickEvent, Slot> onLeftClick,
            @Nullable final BiConsumer<InventoryClickEvent, Slot> onRightClick,
            @Nullable final BiConsumer<InventoryClickEvent, Slot> onShiftLeftClick,
            @Nullable final BiConsumer<InventoryClickEvent, Slot> onShiftRightClick,
            @Nullable final BiConsumer<InventoryClickEvent, Slot> onMiddleClick) {
        this.provider = provider;
        this.onLeftClick = onLeftClick == null ? ((event, slot) -> {
        }) : onLeftClick;
        this.onRightClick = onRightClick == null ? ((event, slot) -> {
        }) : onRightClick;
        this.onShiftLeftClick = onShiftLeftClick == null ? ((event, slot) -> {
        }) : onShiftLeftClick;
        this.onShiftRightClick = onShiftRightClick == null ? ((event, slot) -> {
        }) : onShiftRightClick;
        this.onMiddleClick = onMiddleClick == null ? ((event, slot) -> {
        }) : onMiddleClick;
    }

    public void handleInventoryClick(@NotNull final InventoryClickEvent event) {
        switch (event.getClick()) {
            case LEFT -> this.onLeftClick.accept(event, this);
            case RIGHT -> this.onRightClick.accept(event, this);
            case SHIFT_LEFT -> this.onShiftLeftClick.accept(event, this);
            case SHIFT_RIGHT -> this.onShiftRightClick.accept(event, this);
            case MIDDLE -> this.onMiddleClick.accept(event, this);
            default -> {
            }
        }
    }

    @Override
    public ItemStack getItemStack(@NotNull final Player player) {
        return provider.apply(player);
    }
}
