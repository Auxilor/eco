package com.willfp.eco.internal.spigot.proxy.v1_21_8.enchant

import com.willfp.eco.core.enchant.VanillaEnchantmentOverrides
import it.unimi.dsi.fastutil.objects.Reference2IntMap
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.world.item.enchantment.Enchantment as NMSEnchantment
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.enchantments.CraftEnchantment
import org.bukkit.enchantments.Enchantment

/**
 * Applies vanilla enchantment overrides to the registry values, so bukkit enchantments stay plain
 * [CraftEnchantment]s and keep equality with the [Enchantment] constants.
 */
class VanillaEnchantmentPatcher(
    private val enchantmentRegistry: Registry<NMSEnchantment>
) {
    private val bindValue = Holder.Reference::class.java
        .getDeclaredMethod("bindValue", Any::class.java)
        .apply { isAccessible = true }

    private val byValueField = MappedRegistry::class.java
        .getDeclaredField("byValue")
        .apply { isAccessible = true }

    private val toIdField = MappedRegistry::class.java
        .getDeclaredField("toId")
        .apply { isAccessible = true }

    private val originals = HashMap<NamespacedKey, NMSEnchantment>()

    fun patch(overrides: VanillaEnchantmentOverrides?, customKeys: Set<NamespacedKey>) {
        val holders = enchantmentRegistry.listElements().toList()
            .associateBy { CraftEnchantment(it).key }

        for ((key, holder) in holders) {
            if (key in customKeys) {
                continue
            }

            val original = originals.getOrPut(key) { holder.value() }
            val maxLevel = overrides?.getMaxLevel(key)
            val conflicts = overrides?.getConflicts(key)

            if (maxLevel == null && conflicts == null) {
                bind(holder, original)
                continue
            }

            val definition = original.definition()

            bind(
                holder,
                NMSEnchantment(
                    original.description(),
                    NMSEnchantment.EnchantmentDefinition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        maxLevel ?: definition.maxLevel(),
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()
                    ),
                    conflicts?.let { HolderSet.direct(it.mapNotNull { conflict -> holders[conflict] }) }
                        ?: original.exclusiveSet(),
                    original.effects()
                )
            )
        }
    }

    /**
     * Freezing rebinds every holder from byValue, and ids are looked up by value, so both maps follow the holder.
     */
    @Suppress("UNCHECKED_CAST")
    private fun bind(holder: Holder.Reference<NMSEnchantment>, value: NMSEnchantment) {
        val current = holder.value()

        if (current === value) {
            return
        }

        val byValue = byValueField.get(enchantmentRegistry) as MutableMap<NMSEnchantment, Holder.Reference<NMSEnchantment>>
        val toId = toIdField.get(enchantmentRegistry) as Reference2IntMap<NMSEnchantment>

        byValue.remove(current)
        byValue[value] = holder
        toId.put(value, toId.removeInt(current))

        bindValue.invoke(holder, value)
    }
}
