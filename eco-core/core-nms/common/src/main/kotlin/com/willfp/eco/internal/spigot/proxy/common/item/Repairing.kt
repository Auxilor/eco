package com.willfp.eco.internal.spigot.proxy.common.item

import com.willfp.eco.internal.spigot.proxies.UnitRepair
import com.willfp.eco.internal.spigot.proxy.common.asNMSStack
import kotlin.math.min
import org.bukkit.inventory.ItemStack

/**
 * Vanilla anvil repair math, read off the items themselves rather than reimplemented from their
 * material. Both the repair materials and the durability numbers come from the stack's components,
 * so an item with a custom `minecraft:max_damage` or `minecraft:repairable` behaves the way the
 * server (and a vanilla anvil) says it should.
 *
 * Mirrors `AnvilMenu#createResult`.
 */
object Repairing {
    /** Repairs a quarter of the item's max durability per unit, up to the units available. */
    fun unitRepair(item: ItemStack, repairMaterial: ItemStack): UnitRepair? {
        val stack = item.asNMSStack()

        if (!stack.isDamageableItem || !stack.isValidRepairItem(repairMaterial.asNMSStack())) {
            return null
        }

        val perUnit = stack.maxDamage / 4
        var damage = stack.damageValue
        var units = 0

        var repaired = min(damage, perUnit)
        while (repaired > 0 && units < repairMaterial.amount) {
            damage -= repaired
            units++
            repaired = min(damage, perUnit)
        }

        return if (units == 0) null else UnitRepair(damage, units)
    }

    /** Merges the sacrifice's remaining durability into the item, plus vanilla's 12% bonus. */
    fun combineRepair(item: ItemStack, sacrifice: ItemStack): Int? {
        val stack = item.asNMSStack()

        if (!stack.isDamageableItem) {
            return null
        }

        val other = sacrifice.asNMSStack()
        val durability = (stack.maxDamage - stack.damageValue) +
                (other.maxDamage - other.damageValue) +
                (stack.maxDamage * 12 / 100)

        val damage = (stack.maxDamage - durability).coerceAtLeast(0)

        return if (damage < stack.damageValue) damage else null
    }
}
