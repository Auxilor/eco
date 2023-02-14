package com.willfp.eco.internal.spigot.proxy.common.packet.display

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket
import net.minecraft.resources.ResourceLocation

class PacketAutoRecipe(
    private val plugin: EcoPlugin
) : PacketListener {
    override fun onSend(event: PacketEvent) {
        val packet = event.packet.handle as? ClientboundPlaceGhostRecipePacket ?: return

        if (!plugin.configYml.getBool("displayed-recipes")) {
            return
        }

        if (!EcoPlugin.getPluginNames().contains(packet.recipe.namespace)) {
            return
        }

        if (packet.recipe.path.contains("_displayed")) {
            return
        }

        val fKey = packet.javaClass.getDeclaredField("b")
        fKey.isAccessible = true
        val key = fKey[packet] as ResourceLocation
        fKey[packet] = ResourceLocation(key.namespace, key.path + "_displayed")
    }
}
