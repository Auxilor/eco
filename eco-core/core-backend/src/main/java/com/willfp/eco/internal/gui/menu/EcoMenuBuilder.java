package com.willfp.eco.internal.gui.menu;

import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.core.gui.menu.MenuBuilder;
import com.willfp.eco.core.gui.slot.FillerMask;
import com.willfp.eco.core.gui.slot.Slot;
import com.willfp.eco.internal.gui.slot.EcoFillerSlot;
import com.willfp.eco.util.ListUtils;
import com.willfp.eco.util.StringUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class EcoMenuBuilder implements MenuBuilder {

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

    public EcoMenuBuilder(final int rows) {
        this.rows = rows;
        this.slots = ListUtils.create2DList(rows, 9);
        this.maskSlots = ListUtils.create2DList(rows, 9);
    }

    @Override
    public MenuBuilder setTitle(@NotNull final String title) {
        this.title = StringUtils.translate(title);
        return this;
    }

    @Override
    public MenuBuilder setSlot(final int row,
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

    @Override
    public MenuBuilder setMask(@NotNull final FillerMask mask) {
        this.maskSlots = mask.getMask();
        return this;
    }

    @Override
    public MenuBuilder onClose(@NotNull final Consumer<InventoryCloseEvent> action) {
        this.onClose = action;
        return this;
    }

    @Override
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
                    finalSlot.set(j, new EcoFillerSlot(new ItemStack(Material.AIR)));
                }
            }
        }

        return new EcoMenu(rows, finalSlots, title, onClose);
    }
}
