package com.willfp.eco.internal.gui

import com.willfp.eco.core.gui.GUIFactory
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.slot.SlotBuilder
import com.willfp.eco.core.gui.slot.SlotProvider
import com.willfp.eco.internal.gui.menu.EcoMenuBuilder
import com.willfp.eco.internal.gui.slot.EcoSlotBuilder

class EcoGUIFactory : GUIFactory {
    override fun createSlotBuilder(provider: SlotProvider): SlotBuilder {
        return EcoSlotBuilder(provider)
    }

    override fun createMenuBuilder(rows: Int): MenuBuilder {
        return EcoMenuBuilder(rows)
    }
}