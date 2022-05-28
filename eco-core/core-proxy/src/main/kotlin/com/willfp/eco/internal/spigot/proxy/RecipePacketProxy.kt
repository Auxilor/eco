package com.willfp.eco.internal.spigot.proxy

interface RecipePacketProxy {
    fun splitPacket(packet: Any): List<Any>
}