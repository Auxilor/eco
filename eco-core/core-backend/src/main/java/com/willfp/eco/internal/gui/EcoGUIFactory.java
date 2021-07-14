package com.willfp.eco.internal.gui;

import com.willfp.eco.core.gui.GUIFactory;
import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.core.gui.slot.Slot;
import com.willfp.eco.internal.gui.menu.EcoMenuBuilder;
import com.willfp.eco.internal.gui.slot.EcoSlotBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class EcoGUIFactory implements GUIFactory {
    @Override
    public Slot.Builder createSlotBuilder(@NotNull final Function<Player, ItemStack> provider) {
        return new EcoSlotBuilder(provider);
    }

    @Override
    public Menu.Builder createMenuBuilder(final int rows) {
        return new EcoMenuBuilder(rows);
    }
}
