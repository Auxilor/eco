package com.willfp.eco.internal.gui;

import com.willfp.eco.core.gui.GUIFactory;
import com.willfp.eco.core.gui.menu.MenuBuilder;
import com.willfp.eco.core.gui.slot.SlotBuilder;
import com.willfp.eco.internal.gui.menu.EcoMenuBuilder;
import com.willfp.eco.internal.gui.slot.EcoSlotBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class EcoGUIFactory implements GUIFactory {
    @Override
    public SlotBuilder createSlotBuilder(@NotNull final Function<Player, ItemStack> provider) {
        return new EcoSlotBuilder(provider);
    }

    @Override
    public MenuBuilder createMenuBuilder(final int rows) {
        return new EcoMenuBuilder(rows);
    }
}
