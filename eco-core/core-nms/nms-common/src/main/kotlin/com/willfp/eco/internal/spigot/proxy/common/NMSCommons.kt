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

private lateinit var impl: CommonsProvider

val NBT_TAG_STRING = impl.nbtTagString

fun Mob.toPathfinderMob(): PathfinderMob? =
    impl.toPathfinderMob(this)

fun NamespacedKey.toResourceLocation(): ResourceLocation =
    impl.toResourceLocation(this)

fun ItemStack.asNMSStack(): net.minecraft.world.item.ItemStack =
    impl.asNMSStack(this)

fun ItemStack.mergeIfNeeded(nmsStack: net.minecraft.world.item.ItemStack) =
    impl.asNMSStack(this)

fun LivingEntity.toBukkitEntity(): org.bukkit.entity.LivingEntity? =
    impl.toBukkitEntity(this)

fun <T: EntityGoal<*>> T.getVersionSpecificEntityGoalFactory(): EntityGoalFactory<T>? =
    impl.getVersionSpecificEntityGoalFactory(this)

fun <T: TargetGoal<*>> T.getVersionSpecificEntityGoalFactory(): TargetGoalFactory<T>? =
    impl.getVersionSpecificTargetGoalFactory(this)

interface CommonsProvider {
    val nbtTagString: Int

    fun toPathfinderMob(mob: Mob): PathfinderMob?

    fun toResourceLocation(namespacedKey: NamespacedKey): ResourceLocation

    fun asNMSStack(itemStack: ItemStack): net.minecraft.world.item.ItemStack

    fun mergeIfNeeded(itemStack: ItemStack, nmsStack: net.minecraft.world.item.ItemStack)

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
