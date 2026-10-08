package com.willfp.eco.internal.spigot.proxy.v1_21_8.enchant

import com.willfp.eco.core.enchant.CustomEnchantment
import net.kyori.adventure.text.Component
import net.minecraft.core.Holder
import net.minecraft.world.item.enchantment.Enchantment as NMSEnchantment
import org.bukkit.craftbukkit.enchantments.CraftEnchantment
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.inventory.ItemStack

class EcoCraftEnchantment(
    val customEnchantment: CustomEnchantment,
    holder: Holder<NMSEnchantment>
) : CraftEnchantment(holder) {
    override fun canEnchantItem(item: ItemStack): Boolean {
        return customEnchantment.canEnchantItem(item)
    }

    override fun conflictsWith(other: Enchantment): Boolean {
        return customEnchantment.conflictsWith(other)
    }

    @Deprecated(
        message = "Custom enchantments are not translatable",
        replaceWith = ReplaceWith("this.displayName(level)")
    )
    override fun translationKey(): String {
        return customEnchantment.translationKey
    }

    @Deprecated(
        message = "getName is a legacy Spigot API",
        replaceWith = ReplaceWith("this.displayName(level)")
    )
    override fun getName(): String = customEnchantment.enchantmentKey.key.uppercase()

    override fun getMaxLevel(): Int = customEnchantment.maximumLevel

    override fun getStartLevel(): Int = 1

    @Deprecated(
        message = "getItemTarget is an incompatible Spigot API",
        replaceWith = ReplaceWith("this.canEnchantItem(item)")
    )
    @Suppress("DEPRECATION")
    override fun getItemTarget(): EnchantmentTarget = EnchantmentTarget.ALL

    @Deprecated(
        message = "Treasure enchantments do not exist for custom enchantments",
        replaceWith = ReplaceWith("this.isDiscoverable")
    )
    override fun isTreasure(): Boolean = !customEnchantment.isObtainableThroughEnchanting

    @Deprecated(
        message = "Curses do not exist for custom enchantments"
    )
    override fun isCursed(): Boolean = false

    override fun displayName(level: Int): Component {
        return customEnchantment.displayName(level)
    }

    override fun isTradeable(): Boolean {
        return customEnchantment.isObtainableThroughTrading
    }

    override fun isDiscoverable(): Boolean {
        return customEnchantment.isObtainableThroughDiscovery
    }

    override fun getMinModifiedCost(level: Int): Int {
        return Int.MAX_VALUE
    }

    override fun getMaxModifiedCost(level: Int): Int {
        return Int.MAX_VALUE
    }

    override fun equals(other: Any?): Boolean {
        return other is EcoCraftEnchantment
                && this.customEnchantment.enchantmentKey == other.customEnchantment.enchantmentKey
    }

    override fun hashCode(): Int {
        return this.customEnchantment.enchantmentKey.hashCode()
    }

    override fun toString(): String {
        return "EcoCraftEnchantment(key=$key)"
    }
}
