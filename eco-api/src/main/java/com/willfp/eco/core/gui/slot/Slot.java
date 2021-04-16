package com.willfp.eco.core.gui.slot;

import com.willfp.eco.internal.gui.EcoSlot;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public interface Slot {
    ItemStack getItemStack();

    static Builder builder(@NotNull final ItemStack itemStack) {
        return new Builder(itemStack);
    }

    class Builder {
        private final ItemStack itemStack;

        private BiConsumer<InventoryClickEvent, Slot> onLeftClick = null;

        private BiConsumer<InventoryClickEvent, Slot> onRightClick = null;

        private BiConsumer<InventoryClickEvent, Slot> onShiftLeftClick = null;

        private BiConsumer<InventoryClickEvent, Slot> onShiftRightClick = null;

        private BiConsumer<InventoryClickEvent, Slot> onMiddleClick = null;

        Builder(@NotNull final ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        public Builder onLeftClick(@NotNull final BiConsumer<InventoryClickEvent, Slot> action) {
            this.onLeftClick = action;
            return this;
        }

        public Builder onRightClick(@NotNull final BiConsumer<InventoryClickEvent, Slot> action) {
            this.onRightClick = action;
            return this;
        }

        public Builder onShiftLeftClick(@NotNull final BiConsumer<InventoryClickEvent, Slot> action) {
            this.onShiftLeftClick = action;
            return this;
        }

        public Builder onShiftRightClick(@NotNull final BiConsumer<InventoryClickEvent, Slot> action) {
            this.onShiftRightClick = action;
            return this;
        }

        public Builder onMiddleClick(@NotNull final BiConsumer<InventoryClickEvent, Slot> action) {
            this.onMiddleClick = action;
            return this;
        }

        public Slot build() {
            return new EcoSlot(itemStack, onLeftClick, onRightClick, onShiftLeftClick, onShiftRightClick, onMiddleClick);
        }
    }
}
