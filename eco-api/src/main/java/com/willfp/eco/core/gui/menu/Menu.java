package com.willfp.eco.core.gui.menu;

import com.willfp.eco.core.gui.slot.Slot;
import com.willfp.eco.internal.gui.EcoMenu;
import com.willfp.eco.internal.gui.FillerSlot;
import com.willfp.eco.util.ListUtils;
import com.willfp.eco.util.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public interface Menu {
    /**
     * Get the amount of rows.
     *
     * @return
     */
    int getRows();

    /**
     * Get slot at given row and column.
     *
     * @param row    The row.
     * @param column The column.
     * @return The row.
     */
    Slot getSlot(int row,
                 int column);

    /**
     * Get the menu title.
     *
     * @return The title.
     */
    String getTitle();

    /**
     * Open the inventory for the player.
     *
     * @param player The player.
     * @return The inventory.
     */
    Inventory open(@NotNull Player player);

    /**
     * Create a builder with a given amount of rows.
     *
     * @param rows The rows.
     * @return The builder.
     */
    static Builder builder(final int rows) {
        return new Builder(rows);
    }

    class Builder {
        /**
         * The amount of rows.
         */
        private final int rows;

        /**
         * The title.
         */
        private String title = "Menu";

        /**
         * The mask slots.
         */
        private List<List<Slot>> maskSlots;

        /**
         * The slots.
         */
        private final List<List<Slot>> slots;

        /**
         * The close event handler.
         */
        private Consumer<InventoryCloseEvent> onClose = (event) -> {
        };

        Builder(final int rows) {
            this.rows = rows;
            this.slots = ListUtils.create2DList(rows, 9);
            this.maskSlots = ListUtils.create2DList(rows, 9);
        }

        public Builder setTitle(@NotNull final String title) {
            this.title = StringUtils.translate(title);
            return this;
        }

        public Builder setSlot(final int row,
                               final int column,
                               @NotNull final Slot slot) {
            if (row < 1 || row > this.rows) {
                throw new IllegalArgumentException("Invalid row number!");
            }

            if (column < 1 || column > 9) {
                throw new IllegalArgumentException("Invalid column number!");
            }

            slots.get(row - 1).set(column - 1, slot);
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
            List<List<Slot>> finalSlots = maskSlots;
            for (int i = 0; i < slots.size(); i++) {
                for (int j = 0; j < slots.get(i).size(); j++) {
                    Slot slot = slots.get(i).get(j);
                    if (slot != null) {
                        finalSlots.get(i).set(j, slot);
                    }
                }
            }

            for (List<Slot> finalSlot : finalSlots) {
                for (int j = 0; j < finalSlot.size(); j++) {
                    if (finalSlot.get(j) == null) {
                        finalSlot.set(j, new FillerSlot(new ItemStack(Material.AIR)));
                    }
                }
            }

            return new EcoMenu(rows, finalSlots, title, onClose);
        }
    }
}
