package com.willfp.eco.internal.gui;

import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.core.gui.slot.Slot;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class EcoMenu implements Menu {
    @Getter
    private final int rows;

    private final List<List<Slot>> slots;

    @Getter
    private final String title;

    private final Consumer<InventoryCloseEvent> onClose;

    public EcoMenu(final int rows,
                   @NotNull final List<List<Slot>> slots,
                   @NotNull final String title,
                   @NotNull final Consumer<InventoryCloseEvent> onClose) {
        this.rows = rows;
        this.slots = slots;
        this.title = title;
        this.onClose = onClose;
    }

    @Override
    public Slot getSlot(final int row,
                        final int column) {
        if (row < 1 || row > this.rows) {
            throw new IllegalArgumentException("Invalid row number!");
        }

        if (column < 1 || column > 9) {
            throw new IllegalArgumentException("Invalid column number!");
        }

        return slots.get(row - 1).get(column - 1);
    }

    @Override
    public Inventory open(@NotNull final Player player) {
        Inventory inventory = Bukkit.createInventory(null, rows * 9, title);
        int i = 0;
        for (List<Slot> row : slots) {
            for (Slot item : row) {
                if (i == rows * 9) {
                    break;
                }
                inventory.setItem(i, item.getItemStack());
                i++;
            }
        }


        player.openInventory(inventory);
        MenuHandler.registerMenu(inventory, this);
        return inventory;
    }

    public void handleClose(@NotNull final InventoryCloseEvent event) {
        onClose.accept(event);
    }
}
