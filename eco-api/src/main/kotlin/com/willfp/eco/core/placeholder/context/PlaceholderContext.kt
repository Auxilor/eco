@file:JvmName("PlaceholderContextExtensions")

package com.willfp.eco.core.placeholder.context

import com.willfp.eco.core.placeholder.AdditionalPlayer
import com.willfp.eco.core.placeholder.PlaceholderInjectable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Create a new [PlaceholderContext], with every argument optional.
 *
 * @param player The player, or null for no player.
 * @param item The item, or null for no item.
 * @param injectable The injectable context, or null for an empty one.
 * @param additionalPlayers The additional players, defaulting to none.
 * @return The context.
 * @see PlaceholderContext
 */
@JvmOverloads
fun placeholderContext(
    player: Player? = null,
    item: ItemStack? = null,
    injectable: PlaceholderInjectable? = null,
    additionalPlayers: Collection<AdditionalPlayer> = emptyList()
): PlaceholderContext = PlaceholderContext(player, item, injectable, additionalPlayers)

/**
 * Create a copy of the receiver context, overriding any of its components.
 *
 * @param player The player, defaulting to the receiver's player.
 * @param item The item, defaulting to the receiver's item.
 * @param injectable The injectable context, defaulting to the receiver's injectable context.
 * @param additionalPlayers The additional players, defaulting to the receiver's additional players.
 * @return The copied context.
 * @see PlaceholderContext
 */
fun PlaceholderContext.copy(
    player: Player? = this.player,
    item: ItemStack? = this.itemStack,
    injectable: PlaceholderInjectable? = this.injectableContext,
    additionalPlayers: Collection<AdditionalPlayer> = this.additionalPlayers
): PlaceholderContext = PlaceholderContext(player, item, injectable, additionalPlayers)
