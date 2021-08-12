package com.willfp.eco.spigot.gui

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.PluginDependent
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.Slot
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.inventory.ItemStack

class GUITester(plugin: EcoPlugin) : PluginDependent<EcoPlugin>(plugin), Listener {
    private val menu: Menu = Menu.builder(3)
        .setMask(FillerMask(
            Material.BLACK_STAINED_GLASS_PANE,
            "111111111",
            "100000001",
            "111111111"
        )).setSlot(
            2, 2,
            Slot.builder(ItemStack(Material.RED_STAINED_GLASS_PANE))
                .setCaptive()
                .build()
        ).build()

    @EventHandler
    fun test(event: AsyncPlayerChatEvent) {
        if (!event.message.equals("testgui", true)) {
            return
        }

        plugin.scheduler.run{menu.open(event.player)}
    }
}