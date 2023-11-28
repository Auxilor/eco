package com.willfp.eco.internal.spigot.proxy

import net.kyori.adventure.text.Component
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

interface DisplayNameProxy {
    fun setClientsideDisplayName(entity: LivingEntity, player: Player, displayName: Component, visible: Boolean)
}
