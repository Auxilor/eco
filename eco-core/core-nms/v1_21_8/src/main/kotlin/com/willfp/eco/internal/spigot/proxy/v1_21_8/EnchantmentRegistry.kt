package com.willfp.eco.internal.spigot.proxy.v1_21_8

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.enchant.CustomEnchantment
import com.willfp.eco.core.enchant.VanillaEnchantmentOverrides
import com.willfp.eco.internal.spigot.proxies.EnchantmentRegistryProxy
import com.willfp.eco.internal.spigot.proxy.v1_21_8.enchant.EcoCraftEnchantment
import com.willfp.eco.internal.spigot.proxy.v1_21_8.enchant.ModifiedVanillaCraftEnchantment
import io.papermc.paper.adventure.PaperAdventure
import io.papermc.paper.registry.entry.RegistryTypeMapper
import io.papermc.paper.registry.legacy.DelayedRegistry
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.enchantment.Enchantment as NMSEnchantment
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Registry as BukkitRegistry
import org.bukkit.craftbukkit.CraftRegistry
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.util.CraftNamespacedKey
import org.bukkit.enchantments.Enchantment
import java.lang.reflect.Modifier
import java.util.Collections
import java.util.IdentityHashMap
import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiFunction

class EnchantmentRegistry : EnchantmentRegistryProxy {
    private val enchantmentRegistry =
        (Bukkit.getServer() as CraftServer).server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)

    @Suppress("DEPRECATION")
    private val bukkitRegistry: BukkitRegistry<Enchantment>
        get() = (BukkitRegistry.ENCHANTMENT as DelayedRegistry<Enchantment, *>).delegate()

    private val frozenField = MappedRegistry::class.java
        .getDeclaredField("frozen")
        .apply { isAccessible = true }

    private val allTagsField = MappedRegistry::class.java
        .getDeclaredField("allTags")
        .apply { isAccessible = true }

    private val unregisteredIntrusiveHoldersField = MappedRegistry::class.java
        .getDeclaredField("unregisteredIntrusiveHolders")
        .apply { isAccessible = true }

    private val minecraftToBukkitField = CraftRegistry::class.java
        .getDeclaredField("minecraftToBukkit")
        .apply { isAccessible = true }

    private val cacheField = CraftRegistry::class.java
        .getDeclaredField("cache")
        .apply { isAccessible = true }

    private val customEnchantments = ConcurrentHashMap<NamespacedKey, EcoCraftEnchantment>()

    @Volatile
    private var vanillaOverrides: VanillaEnchantmentOverrides? = null

    override fun unfreeze() {
        val minecraftToBukkit = BiFunction<NamespacedKey, NMSEnchantment, Enchantment?> { key, _ ->
            customEnchantments[key] ?: enchantmentRegistry.get(CraftNamespacedKey.toMinecraft(key))
                .map { ModifiedVanillaCraftEnchantment(key, it) { vanillaOverrides } }
                .orElse(null)
        }

        // The mapper is typed non-null in Paper but returns null for unknown keys.
        @Suppress("UNCHECKED_CAST")
        minecraftToBukkitField.set(
            bukkitRegistry,
            RegistryTypeMapper(minecraftToBukkit as BiFunction<NamespacedKey, NMSEnchantment, Enchantment>)
        )

        clearBukkitCache()

        frozenField.set(enchantmentRegistry, false)
        unregisteredIntrusiveHoldersField.set(
            enchantmentRegistry,
            IdentityHashMap<NMSEnchantment, Holder.Reference<NMSEnchantment>>()
        )

        // The unbound tag set's class is package-private, so its factory is found by shape.
        val unboundTagSet = MappedRegistry::class.java
            .declaredClasses[0]
            .declaredMethods
            .filter { Modifier.isStatic(it.modifiers) }
            .filter { it.parameterCount == 0 }[0]
            .apply { isAccessible = true }
            .invoke(null)

        allTagsField.set(enchantmentRegistry, unboundTagSet)
    }

    override fun freeze() {
        enchantmentRegistry.freeze()
    }

    override fun register(enchantment: CustomEnchantment): Enchantment {
        clearBukkitCache()

        val location = CraftNamespacedKey.toMinecraft(enchantment.enchantmentKey)

        if (!enchantmentRegistry.containsKey(location)) {
            createVanillaEnchantment(enchantment).let {
                enchantmentRegistry.createIntrusiveHolder(it)
                Registry.register(enchantmentRegistry, location, it)
            }
        }

        val holder = enchantmentRegistry.get(location)
            .orElseThrow { IllegalStateException("Enchantment ${enchantment.enchantmentKey} wasn't registered") }

        return EcoCraftEnchantment(enchantment, holder).also {
            customEnchantments[enchantment.enchantmentKey] = it
        }
    }

    override fun unregister(enchantment: CustomEnchantment) {
        customEnchantments.remove(enchantment.enchantmentKey)
    }

    override fun setVanillaOverrides(overrides: VanillaEnchantmentOverrides?) {
        vanillaOverrides = overrides
    }

    override fun getCustomEnchantment(enchantment: Enchantment): CustomEnchantment? {
        return (enchantment as? EcoCraftEnchantment)?.customEnchantment
    }

    // Filled lazily from Registry#get, on many region threads under Folia, and holds nulls.
    private fun clearBukkitCache() {
        cacheField.set(
            bukkitRegistry,
            if (Prerequisite.HAS_FOLIA.isMet) Collections.synchronizedMap(HashMap<NamespacedKey, Enchantment>())
            else HashMap<NamespacedKey, Enchantment>()
        )
    }

    private fun createVanillaEnchantment(enchantment: CustomEnchantment): NMSEnchantment {
        val definition = NMSEnchantment.definition(
            HolderSet.empty(),
            1,
            enchantment.maximumLevel,
            NMSEnchantment.constantCost(1),
            NMSEnchantment.constantCost(1),
            0
        )

        return NMSEnchantment(
            PaperAdventure.asVanilla(enchantment.registryDescription),
            definition,
            HolderSet.empty(),
            DataComponentMap.EMPTY
        )
    }
}
