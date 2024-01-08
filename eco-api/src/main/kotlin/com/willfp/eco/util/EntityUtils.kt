@file:JvmName("EntityUtilsExtensions")

package com.willfp.eco.util

import net.kyori.adventure.text.Component
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

/** @see EntityUtils.setClientsideDisplayName */
fun LivingEntity.setClientsideDisplayName(player: Player, displayName: Component, visible: Boolean) {
    EntityUtils.setClientsideDisplayName(this, player, displayName, visible)
}
