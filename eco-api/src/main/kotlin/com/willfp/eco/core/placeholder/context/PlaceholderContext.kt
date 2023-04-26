@file:JvmName("PlaceholderContextExtensions")

package com.willfp.eco.core.placeholder.context

import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.AdditionalPlayer
import com.willfp.eco.core.placeholder.PlaceholderInjectable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/** @see PlaceholderContext */
@JvmOverloads
fun placeholderContext(
    player: Player? = null,
    item: ItemStack? = null,
    injectable: PlaceholderInjectable = PlaceholderManager.EMPTY_INJECTABLE,
    additionalPlayers: Collection<AdditionalPlayer> = emptyList()
): PlaceholderContext = PlaceholderContext(player, item, injectable, additionalPlayers)
