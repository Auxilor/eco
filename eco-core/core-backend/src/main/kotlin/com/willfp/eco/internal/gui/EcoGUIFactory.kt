package com.willfp.eco.internal.gui

import com.willfp.eco.core.gui.GUIFactory
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.slot.SlotBuilder
import com.willfp.eco.core.gui.slot.functional.SlotProvider
import com.willfp.eco.internal.gui.menu.EcoMenuBuilder
import com.willfp.eco.internal.gui.page.DelegateMenu
import com.willfp.eco.internal.gui.slot.EcoSlotBuilder

object EcoGUIFactory : GUIFactory {
    override fun createSlotBuilder(provider: SlotProvider): SlotBuilder {
        return EcoSlotBuilder(provider)
    }

    override fun createMenuBuilder(rows: Int): MenuBuilder {
        return EcoMenuBuilder(rows)
    }

    override fun blendMenuState(base: Menu, additional: Menu): Menu {
        return DelegateMenu(base, additional)
    }
}
