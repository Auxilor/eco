@file:JvmName("PlaceholderContextExtensions")

package com.willfp.eco.core.placeholder.context

import com.willfp.eco.core.placeholder.AdditionalPlayer
import com.willfp.eco.core.placeholder.PlaceholderInjectable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/** @see PlaceholderContext */
@JvmOverloads
fun placeholderContext(
    player: Player? = null,
    item: ItemStack? = null,
    injectable: PlaceholderInjectable? = null,
    additionalPlayers: Collection<AdditionalPlayer> = emptyList()
): PlaceholderContext = PlaceholderContext(player, item, injectable, additionalPlayers)

/** @see PlaceholderContext */
fun PlaceholderContext.copy(
    player: Player? = this.player,
    item: ItemStack? = this.itemStack,
    injectable: PlaceholderInjectable? = this.injectableContext,
    additionalPlayers: Collection<AdditionalPlayer> = this.additionalPlayers
): PlaceholderContext = PlaceholderContext(player, item, injectable, additionalPlayers)
