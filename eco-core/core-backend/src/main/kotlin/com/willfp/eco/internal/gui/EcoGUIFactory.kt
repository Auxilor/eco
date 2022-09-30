package com.willfp.eco.internal.gui

import com.willfp.eco.core.gui.GUIFactory
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.slot.functional.SlotProvider
import com.willfp.eco.internal.gui.menu.EcoMenuBuilder
import com.willfp.eco.internal.gui.page.MergedStateMenu
import com.willfp.eco.internal.gui.slot.EcoSlotBuilder

object EcoGUIFactory : GUIFactory {
    override fun createSlotBuilder(provider: SlotProvider) =
        EcoSlotBuilder(provider)

    override fun createMenuBuilder(rows: Int): MenuBuilder =
        EcoMenuBuilder(rows)

    override fun blendMenuState(base: Menu, additional: Menu): Menu =
        MergedStateMenu(base, additional)
}
