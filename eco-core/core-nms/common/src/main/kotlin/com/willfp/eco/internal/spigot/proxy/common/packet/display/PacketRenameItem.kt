package com.willfp.eco.internal.spigot.proxy.common.packet.display

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.internal.spigot.proxy.common.asNMSStack
import net.minecraft.core.component.DataComponents
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket
import org.bukkit.inventory.AnvilInventory

/**
 * The client fills the anvil rename box with the name it was sent, which display modules may have
 * changed. Sending that name back unchanged would rename the item to it, so it is swapped for what
 * the client would have sent had it seen the real item.
 */
object PacketRenameItem : PacketListener {
    override fun onReceive(event: PacketEvent) {
        val packet = event.packet.handle as? ServerboundRenameItemPacket ?: return
        val item = (event.player.openInventory.topInventory as? AnvilInventory)?.getItem(0)

        if (item == null || item.type.isAir) return

        val realItem = item.asNMSStack()
        val realName = realItem.hoverName.string
        val displayedName = Display.display(item.clone(), event.player).asNMSStack().hoverName.string

        if (packet.name == displayedName && displayedName != realName) {
            event.packet.handle = ServerboundRenameItemPacket(
                if (realItem.has(DataComponents.CUSTOM_NAME)) realName else ""
            )
        }
    }
}
