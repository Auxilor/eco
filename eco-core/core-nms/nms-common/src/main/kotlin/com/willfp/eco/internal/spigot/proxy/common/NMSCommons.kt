package com.willfp.eco.internal.spigot.proxy.common

import com.willfp.eco.core.entities.ai.EntityGoal
import com.willfp.eco.core.entities.ai.TargetGoal
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.TargetGoalFactory
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import org.bukkit.NamespacedKey
import org.bukkit.entity.Mob
import org.bukkit.inventory.ItemStack

val commonsProvider: CommonsProvider
    get() = impl

private lateinit var impl: CommonsProvider

interface CommonsProvider {
    val nbtTagString: Int

    fun toPathfinderMob(mob: Mob): PathfinderMob?

    fun toResourceLocation(namespacedKey: NamespacedKey): ResourceLocation

    fun asNMSStack(itemStack: ItemStack): net.minecraft.world.item.ItemStack

    fun mergeIfNeeded(itemStack: ItemStack, nmsStack: net.minecraft.world.item.ItemStack)

    fun toNMSClass(bukkit: Class<out org.bukkit.entity.LivingEntity>): Class<out LivingEntity>?

    fun toBukkitEntity(entity: LivingEntity): org.bukkit.entity.LivingEntity?

    fun <T : EntityGoal<*>> getVersionSpecificEntityGoalFactory(goal: T): EntityGoalFactory<T>? {
        return null
    }

    fun <T : TargetGoal<*>> getVersionSpecificTargetGoalFactory(goal: T): TargetGoalFactory<T>? {
        return null
    }

    companion object {
        fun setIfNeeded(provider: CommonsProvider) {
            if (::impl.isInitialized) {
                return
            }

            impl = provider
        }
    }
}
