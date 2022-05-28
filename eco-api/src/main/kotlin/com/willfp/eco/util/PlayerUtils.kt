@file:JvmName("PlayerUtilsExtensions")

package com.willfp.eco.util

import net.kyori.adventure.audience.Audience
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

/** @see PlayerUtils.getSavedDisplayName */
val OfflinePlayer.savedDisplayName: String
    get() = PlayerUtils.getSavedDisplayName(this)

/** @see PlayerUtils.getAudience */
fun Player.asAudience(): Audience =
    PlayerUtils.getAudience(this)

/** @see PlayerUtils.getAudience */
fun CommandSender.asAudience(): Audience =
    PlayerUtils.getAudience(this)

/** @see PlayerUtils.runExempted */
fun Player.runExempted(action: () -> Unit) =
    PlayerUtils.runExempted(this, action)

/** @see PlayerUtils.tryAsPlayer */
fun Entity?.tryAsPlayer(): Player? =
    PlayerUtils.tryAsPlayer(this)
