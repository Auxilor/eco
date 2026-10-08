package com.willfp.eco.core.enchant

import com.willfp.eco.core.Eco
import org.bukkit.enchantments.Enchantment

/**
 * Registers [CustomEnchantment]s into the server enchantment registry.
 *
 * Call [unfreezeRegistry], then [register] each enchantment, then [freezeRegistry].
 */
object CustomEnchantments {
    /**
     * Unfreeze the enchantment registry and route bukkit lookups through eco.
     */
    @JvmStatic
    fun unfreezeRegistry() =
        Eco.get().unfreezeEnchantmentRegistry()

    /**
     * Freeze the enchantment registry.
     */
    @JvmStatic
    fun freezeRegistry() =
        Eco.get().freezeEnchantmentRegistry()

    /**
     * Register an enchantment. The registry must be unfrozen.
     *
     * @param enchantment The enchantment.
     * @return The bukkit enchantment.
     */
    @JvmStatic
    fun register(enchantment: CustomEnchantment): Enchantment =
        Eco.get().registerCustomEnchantment(enchantment)

    /**
     * Stop bukkit lookups resolving to an enchantment.
     *
     * The vanilla registry entry stays, as entries can't be removed.
     *
     * @param enchantment The enchantment.
     */
    @JvmStatic
    fun unregister(enchantment: CustomEnchantment) =
        Eco.get().unregisterCustomEnchantment(enchantment)

    /**
     * Set the overrides for vanilla enchantments.
     *
     * @param overrides The overrides, or null to clear them.
     */
    @JvmStatic
    fun setVanillaOverrides(overrides: VanillaEnchantmentOverrides?) =
        Eco.get().setVanillaEnchantmentOverrides(overrides)

    /**
     * Get the custom enchantment behind a bukkit enchantment.
     *
     * @param enchantment The bukkit enchantment.
     * @return The custom enchantment, or null if it isn't one.
     */
    @JvmStatic
    fun getCustomEnchantment(enchantment: Enchantment): CustomEnchantment? =
        Eco.get().getCustomEnchantment(enchantment)
}

/** @see CustomEnchantments.getCustomEnchantment */
val Enchantment.customEnchantment: CustomEnchantment?
    get() = CustomEnchantments.getCustomEnchantment(this)
