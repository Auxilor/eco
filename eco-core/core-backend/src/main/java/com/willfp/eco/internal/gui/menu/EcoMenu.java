package com.willfp.eco.internal.gui.menu;

import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.core.gui.slot.FillerSlot;
import com.willfp.eco.core.gui.slot.Slot;
import com.willfp.eco.internal.gui.slot.EcoFillerSlot;
import com.willfp.eco.util.StringUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class EcoMenu implements Menu {
    /**
     * The amount of rows.
     */
    @Getter
    private final int rows;

    /**
     * 2D list of slots.
     */
    private final List<List<Slot>> slots;

    /**
     * Menu title.
     */
    @Getter
    private final String title;

    /**
     * Handler on close.
     */
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

        Slot slot = slots.get(row - 1).get(column - 1);
        if (slot instanceof FillerSlot fillerSlot) {
            slots.get(row - 1).set(
                    column - 1,
                    new EcoFillerSlot(fillerSlot.getItemStack())
            );

            return getSlot(row, column);
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
                ItemStack slotItem = item.getItemStack(player);
                ItemMeta meta = slotItem.getItemMeta();
                if (meta != null) {
                    List<String> lore = meta.getLore();
                    if (lore != null) {
                        lore.replaceAll(s -> StringUtils.format(s, player));
                        meta.setLore(lore);
                    }
                    slotItem.setItemMeta(meta);
                }
                inventory.setItem(i, slotItem);
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
