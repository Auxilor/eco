package com.willfp.eco.core.gui;

import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.core.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface GUIFactory {
    /**
     * Create slot builder.
     *
     * @param provider The provider.
     * @return The builder.
     */
    Slot.Builder createSlotBuilder(@NotNull Function<Player, ItemStack> provider);

    /**
     * Create menu builder.
     *
     * @param rows The amount of rows.
     * @return The builder.
     */
    Menu.Builder createMenuBuilder(int rows);
}
