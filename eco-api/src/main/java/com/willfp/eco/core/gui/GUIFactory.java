package com.willfp.eco.core.gui;

import com.willfp.eco.core.gui.menu.MenuBuilder;
import com.willfp.eco.core.gui.slot.SlotBuilder;
import com.willfp.eco.core.gui.slot.SlotProvider;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Internal component used by {@link com.willfp.eco.core.gui.menu.Menu#builder(int)}
 * and {@link com.willfp.eco.core.gui.slot.Slot#builder(ItemStack)}.
 */
public interface GUIFactory {
    /**
     * Create slot builder.
     *
     * @param provider The provider.
     * @return The builder.
     */
    SlotBuilder createSlotBuilder(@NotNull SlotProvider provider);

    /**
     * Create menu builder.
     *
     * @param rows The amount of rows.
     * @return The builder.
     */
    MenuBuilder createMenuBuilder(int rows);
}
