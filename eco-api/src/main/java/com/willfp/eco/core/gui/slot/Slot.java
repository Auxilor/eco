package com.willfp.eco.core.gui.slot;

import com.willfp.eco.internal.gui.EcoSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface Slot {
    ItemStack getItemStack(@NotNull Player player);

    static Builder builder(@NotNull final ItemStack itemStack) {
        return new Builder((player) -> itemStack);
    }

    static Builder builder(@NotNull final Function<Player, ItemStack> provider) {
        return new Builder(provider);
    }

    class Builder {
        private final Function<Player, ItemStack> provider;

        private BiConsumer<InventoryClickEvent, Slot> onLeftClick = null;

        private BiConsumer<InventoryClickEvent, Slot> onRightClick = null;

        private BiConsumer<InventoryClickEvent, Slot> onShiftLeftClick = null;

        private BiConsumer<InventoryClickEvent, Slot> onShiftRightClick = null;

        private BiConsumer<InventoryClickEvent, Slot> onMiddleClick = null;

        Builder(@NotNull final Function<Player, ItemStack> provider) {
            this.provider = provider;
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
            return new EcoSlot(provider, onLeftClick, onRightClick, onShiftLeftClick, onShiftRightClick, onMiddleClick);
        }
    }
}
