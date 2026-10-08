package com.willfp.eco.core.enchant

import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

/**
 * A plugin-defined enchantment, registered into the server enchantment registry with
 * [CustomEnchantments.register].
 *
 * The bukkit [Enchantment] the server sees delegates to this.
 */
interface CustomEnchantment {
    /**
     * The key.
     */
    val enchantmentKey: NamespacedKey

    /**
     * The max level.
     */
    val maximumLevel: Int

    /**
     * The name stored on the vanilla enchantment.
     */
    val registryDescription: Component

    /**
     * The translation key.
     */
    val translationKey: String

    /**
     * If the enchantment can be obtained through an enchanting table.
     */
    val isObtainableThroughEnchanting: Boolean

    /**
     * If the enchantment can be obtained through villager trades.
     */
    val isObtainableThroughTrading: Boolean

    /**
     * If the enchantment can be obtained through loot.
     */
    val isObtainableThroughDiscovery: Boolean

    /**
     * Get the display name at a certain [level].
     */
    fun displayName(level: Int): Component

    /**
     * Get if this enchantment can be applied to [item].
     */
    fun canEnchantItem(item: ItemStack): Boolean

    /**
     * Get if this enchantment conflicts with [other].
     */
    fun conflictsWith(other: Enchantment): Boolean
}
