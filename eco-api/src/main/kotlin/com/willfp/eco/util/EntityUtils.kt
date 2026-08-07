@file:JvmName("EntityUtilsExtensions")

package com.willfp.eco.util

import net.kyori.adventure.text.Component
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

/**
 * Set the display name of this entity as seen by a single player.
 *
 * The change is client-side only: it is sent to the given player and does not modify the
 * entity's actual display name on the server or for anyone else.
 *
 * @param player      The player to send the display name to.
 * @param displayName The display name to show.
 * @param visible     If the display name should be forcibly visible.
 * @see EntityUtils.setClientsideDisplayName
 */
fun LivingEntity.setClientsideDisplayName(player: Player, displayName: Component, visible: Boolean) {
    EntityUtils.setClientsideDisplayName(this, player, displayName, visible)
}
