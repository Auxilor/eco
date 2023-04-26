@file:JvmName("PlaceholderContextExtensions")

package com.willfp.eco.core.placeholder.context

import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.AdditionalPlayer
import com.willfp.eco.core.placeholder.PlaceholderInjectable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Kotlin version of [PlaceholderContext].
 */
data class KPlaceholderContext internal constructor(
    val player: Player?,
    val item: ItemStack?,
    val injectableContext: PlaceholderInjectable,
    val additionalPlayers: Collection<AdditionalPlayer>
) : PlaceholderContext(player, item, injectableContext, additionalPlayers) {
    override fun withInjectableContext(injectableContext: PlaceholderInjectable): KPlaceholderContext {
        return this.copy(
            injectableContext = MergedInjectableContext(this.injectableContext, injectableContext),
        )
    }
}

/** @see PlaceholderContext */
@JvmOverloads
fun placeholderContext(
    player: Player? = null,
    item: ItemStack? = null,
    injectable: PlaceholderInjectable = PlaceholderManager.EMPTY_INJECTABLE,
    additionalPlayers: Collection<AdditionalPlayer> = emptyList()
): KPlaceholderContext = KPlaceholderContext(player, item, injectable, additionalPlayers)
