package com.willfp.eco.core.gui.slot;

import com.willfp.eco.internal.gui.EcoSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface Slot {
    /**
     * Get the ItemStack that would be shown to a player.
     *
     * @param player The player.
     * @return The ItemStack.
     */
    ItemStack getItemStack(@NotNull Player player);

    /**
     * Create a builder for an ItemStack.
     *
     * @param itemStack The ItemStack.
     * @return The builder.
     */
    static Builder builder(@NotNull final ItemStack itemStack) {
        return new Builder((player) -> itemStack);
    }

    /**
     * Create a builder for a player-specific ItemStack.
     *
     * @param provider The provider.
     * @return The builder.
     */
    static Builder builder(@NotNull final Function<Player, ItemStack> provider) {
        return new Builder(provider);
    }

    class Builder {
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
