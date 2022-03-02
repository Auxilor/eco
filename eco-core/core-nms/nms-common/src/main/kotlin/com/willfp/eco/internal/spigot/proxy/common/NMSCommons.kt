package com.willfp.eco.internal.spigot.proxy.common

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import org.bukkit.NamespacedKey
import org.bukkit.entity.Mob
import org.bukkit.inventory.ItemStack
import java.util.Optional

val commonsProvider: CommonsProvider
    get() = impl

private lateinit var impl: CommonsProvider

interface CommonsProvider {
    fun toPathfinderMob(mob: Mob): PathfinderMob?
    fun toResourceLocation(namespacedKey: NamespacedKey): ResourceLocation
    fun toNMSStack(itemStack: ItemStack): net.minecraft.world.item.ItemStack
    fun toNMSClass(bukkit: Class<out org.bukkit.entity.LivingEntity>): Optional<Class<out LivingEntity>>
    fun toBukkitEntity(entity: LivingEntity): org.bukkit.entity.LivingEntity?

    companion object {
        fun setIfNeeded(provider: CommonsProvider) {
            if (::impl.isInitialized) {
                return
            }

            impl = provider
        }
    }
}
