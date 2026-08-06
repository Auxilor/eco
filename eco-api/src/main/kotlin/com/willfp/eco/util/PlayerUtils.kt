@file:JvmName("PlayerUtilsExtensions")

package com.willfp.eco.util

import net.kyori.adventure.audience.Audience
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

/**
 * The display name stored in this player's eco profile, readable while they are offline.
 *
 * If the player is currently online, the stored value is refreshed from their live display
 * name before being read. If nothing has ever been stored, their name is returned instead.
 *
 * @see PlayerUtils.getSavedDisplayName
 */
val OfflinePlayer.savedDisplayName: String
    get() = PlayerUtils.getSavedDisplayName(this)

/**
 * The name stored in this player's eco profile, readable while they are offline.
 *
 * If the player is currently online, the stored value is refreshed from their live name
 * before being read. If nothing has ever been stored, their name is returned instead.
 *
 * @see PlayerUtils.getSavedName
 */
val OfflinePlayer.savedName: String
    get() = PlayerUtils.getSavedName(this)

/**
 * The health stored in this player's eco profile, readable while they are offline, in half-hearts.
 *
 * This is only as recent as the last call to [saveHealth]; it is never refreshed automatically.
 * Defaults to 20.0 if no health has ever been saved for the player.
 *
 * @see PlayerUtils.getSavedHealth
 */
val OfflinePlayer.savedHealth: Double
    get() = PlayerUtils.getSavedHealth(this)

/**
 * Store this player's current health in their eco profile, so it can be read back with
 * [savedHealth] while they are offline.
 *
 * @see PlayerUtils.saveHealth
 */
fun Player.saveHealth() =
    PlayerUtils.saveHealth(this)

/**
 * Get this player as an Adventure audience.
 *
 * @return The audience, or an empty audience if no audience is available for the player.
 * @see PlayerUtils.getAudience
 */
fun Player.asAudience(): Audience =
    PlayerUtils.getAudience(this)

/**
 * Get this command sender as an Adventure audience.
 *
 * @return The audience, or an empty audience if no audience is available for the sender.
 * @see PlayerUtils.getAudience
 */
fun CommandSender.asAudience(): Audience =
    PlayerUtils.getAudience(this)

/**
 * Run an action with this player exempted from anticheat checks.
 *
 * The player is always unexempted afterwards, even if the action throws.
 *
 * @param action The action to run.
 * @see PlayerUtils.runExempted
 */
fun Player.runExempted(action: () -> Unit) =
    PlayerUtils.runExempted(this, action)

/**
 * Try to resolve a player from this entity.
 *
 * Resolves the entity itself if it is a player, the shooter if it is a projectile shot by a
 * player, or the owner if it is a tameable entity tamed by a player.
 *
 * @return The player, or null if the receiver is null or no player could be resolved.
 * @see PlayerUtils.tryAsPlayer
 */
fun Entity?.tryAsPlayer(): Player? =
    PlayerUtils.tryAsPlayer(this)

/**
 * Give this player an amount of experience, optionally repairing their mending items first.
 *
 * @param amount       The amount of experience to give.
 * @param applyMending If items enchanted with Mending should be repaired, with the same
 *                     behaviour as the player picking up experience orbs.
 * @see PlayerUtils.giveExpAndApplyMending
 */
fun Player.giveExpAndApplyMending(amount: Int, applyMending: Boolean) =
    PlayerUtils.giveExpAndApplyMending(this, amount, applyMending)
