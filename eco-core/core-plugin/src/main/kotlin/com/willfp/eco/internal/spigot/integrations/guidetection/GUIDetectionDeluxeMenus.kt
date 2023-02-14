package com.willfp.eco.internal.spigot.integrations.guidetection

import com.extendedclip.deluxemenus.menu.Menu
import com.willfp.eco.core.integrations.guidetection.GUIDetectionIntegration
import org.bukkit.entity.Player

class GUIDetectionDeluxeMenus: GUIDetectionIntegration {
    override fun hasGUIOpen(player: Player): Boolean {
        return Menu.inMenu(player)
    }

    override fun getPluginName(): String {
        return "DeluxeMenus"
    }
}
