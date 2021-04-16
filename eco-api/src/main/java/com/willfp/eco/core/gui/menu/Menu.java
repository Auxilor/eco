package com.willfp.eco.core.gui.menu;

import com.willfp.eco.core.gui.slot.Slot;
import com.willfp.eco.internal.gui.EcoMenu;
import com.willfp.eco.internal.gui.FillerSlot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface Menu {
    int getRows();

    Slot getSlot(int row,
                 int column);

    String getTitle();

    Inventory open(@NotNull Player player);

    static Builder builder(final int rows) {
        return new Builder(rows);
    }

    class Builder {
        private final int rows;
        private String title = "Menu";
        private Slot[][] maskSlots;
        private final Slot[][] slots;
        private Consumer<InventoryCloseEvent> onClose = (event) -> {
        };

        Builder(final int rows) {
            this.rows = rows;
            this.slots = new Slot[rows][9];
            this.maskSlots = new Slot[rows][9];
        }

        public Builder setTitle(@NotNull final String title) {
            this.title = title;
            return this;
        }

        public Builder setSlot(final int row,
                               final int column,
                               @NotNull final Slot slot) {
            if (row < 0 || row > this.rows - 1) {
                throw new IllegalArgumentException("Invalid row number!");
            }

            if (column < 0 || column > 8) {
                throw new IllegalArgumentException("Invalid column number!");
            }

            slots[row][column] = slot;
            return this;
        }

        public Builder setMask(@NotNull final FillerMask mask) {
            this.maskSlots = mask.getMask();
            return this;
        }

        public Builder onClose(@NotNull final Consumer<InventoryCloseEvent> action) {
            this.onClose = action;
            return this;
        }

        public Menu build() {
            Slot[][] finalSlots = maskSlots;
            for (int i = 0; i < slots.length; i++) {
                for (int j = 0; j < slots[i].length; j++) {
                    Slot slot = slots[i][j];
                    if (slot != null) {
                        finalSlots[i][j] = slot;
                    }
                }
            }

            for (int i = 0; i < finalSlots.length; i++) {
                for (int j = 0; j < finalSlots[i].length; j++) {
                    if (finalSlots[i][j] == null) {
                        finalSlots[i][j] = new FillerSlot(new ItemStack(Material.AIR));
                    }
                }
            }

            return new EcoMenu(rows, finalSlots, title, onClose);
        }
    }
}
