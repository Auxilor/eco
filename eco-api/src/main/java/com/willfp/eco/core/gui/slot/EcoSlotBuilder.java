package com.willfp.eco.core.gui.slot;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

class EcoSlotBuilder implements Slot.Builder {
    /**
     * Provider.
     */
    private final Function<Player, ItemStack> provider;

    /**
     * Left click handler.
     */
    private BiConsumer<InventoryClickEvent, Slot> onLeftClick = null;

    /**
     * Right click handler.
     */
    private BiConsumer<InventoryClickEvent, Slot> onRightClick = null;

    /**
     * Shift-Left-Click handler.
     */
    private BiConsumer<InventoryClickEvent, Slot> onShiftLeftClick = null;

    /**
     * Shift-Right-Click handler.
     */
    private BiConsumer<InventoryClickEvent, Slot> onShiftRightClick = null;

    /**
     * Middle click handler.
     */
    private BiConsumer<InventoryClickEvent, Slot> onMiddleClick = null;

    EcoSlotBuilder(@NotNull final Function<Player, ItemStack> provider) {
        this.provider = provider;
    }

    public Slot.Builder onLeftClick(@NotNull final BiConsumer<InventoryClickEvent, Slot> action) {
        this.onLeftClick = action;
        return this;
    }

    public Slot.Builder onRightClick(@NotNull final BiConsumer<InventoryClickEvent, Slot> action) {
        this.onRightClick = action;
        return this;
    }

    public Slot.Builder onShiftLeftClick(@NotNull final BiConsumer<InventoryClickEvent, Slot> action) {
        this.onShiftLeftClick = action;
        return this;
    }

    public Slot.Builder onShiftRightClick(@NotNull final BiConsumer<InventoryClickEvent, Slot> action) {
        this.onShiftRightClick = action;
        return this;
    }

    public Slot.Builder onMiddleClick(@NotNull final BiConsumer<InventoryClickEvent, Slot> action) {
        this.onMiddleClick = action;
        return this;
    }

    public Slot build() {
        return new EcoSlot(provider, onLeftClick, onRightClick, onShiftLeftClick, onShiftRightClick, onMiddleClick);
    }
}
