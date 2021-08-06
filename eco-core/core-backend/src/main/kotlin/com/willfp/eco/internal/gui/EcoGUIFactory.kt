package com.willfp.eco.internal.gui

import com.willfp.eco.core.gui.GUIFactory
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.slot.SlotBuilder
import com.willfp.eco.internal.gui.menu.EcoMenuBuilder
import com.willfp.eco.internal.gui.slot.EcoSlotBuilder
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.Function

class EcoGUIFactory : GUIFactory {
    override fun createSlotBuilder(provider: Function<Player, ItemStack>): SlotBuilder {
        return EcoSlotBuilder(provider)
    }

    override fun createMenuBuilder(rows: Int): MenuBuilder {
        return EcoMenuBuilder(rows)
    }
}