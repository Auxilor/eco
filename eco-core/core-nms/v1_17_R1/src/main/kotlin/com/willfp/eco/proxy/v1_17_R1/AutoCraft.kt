package com.willfp.eco.proxy.v1_17_R1

import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket
import net.minecraft.resources.ResourceLocation
import proxy.AutoCraftProxy

class AutoCraft : AutoCraftProxy {
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    override fun modifyPacket(packet: Any) {
        val recipePacket = packet as ClientboundPlaceGhostRecipePacket
        val fKey = recipePacket.javaClass.getDeclaredField("b")
        fKey.isAccessible = true
        val key = fKey[recipePacket] as ResourceLocation
        fKey[recipePacket] = ResourceLocation(key.namespace, key.path + "_displayed")
    }
}