package com.willfp.eco.internal.spigot.recipes.workstation

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.core.recipe.workstation.GrindstoneRecipe
import com.willfp.eco.core.recipe.workstation.WorkstationRecipes
import com.willfp.eco.internal.spigot.proxies.WorkstationPacketProxy
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType

class GrindstonePacketHandler(private val plugin: EcoPlugin) : PacketListener {

    override fun onReceive(event: PacketEvent) {
        val slotNum = plugin.getProxy(WorkstationPacketProxy::class.java)
            .getContainerClickSlot(event.packet) ?: return

        if (slotNum != 0 && slotNum != 1) return

        val player = event.player
        if (player.openInventory.topInventory.type != InventoryType.GRINDSTONE) return

        val cursor = player.itemOnCursor
        if (cursor == null || cursor.type.isAir) return

        val matches = WorkstationRecipes.getAll(GrindstoneRecipe::class.java)
            .any { recipe ->
                when (slotNum) {
                    0 -> recipe.item1.matches(cursor)
                    1 -> recipe.item2?.matches(cursor) == true
                    else -> false
                }
            }
        if (!matches) return

        event.isCancelled = true

        Bukkit.getScheduler().runTask(plugin, Runnable {
            val topInventory = player.openInventory.topInventory
            if (topInventory.type != InventoryType.GRINDSTONE) return@Runnable

            val current = topInventory.getItem(slotNum)
            if (current != null && !current.type.isAir) return@Runnable

            val toPlace = cursor.clone().apply { amount = 1 }
            topInventory.setItem(slotNum, toPlace)
            if (cursor.amount <= 1) player.setItemOnCursor(null)
            else cursor.amount--
            player.updateInventory()
        })
    }
}
