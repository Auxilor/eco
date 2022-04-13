package com.willfp.eco.internal.spigot.proxy.common

import com.willfp.eco.core.entities.ai.EntityGoal
import com.willfp.eco.core.entities.ai.TargetGoal
import com.willfp.eco.internal.spigot.proxy.common.ai.EntityGoalFactory
import com.willfp.eco.internal.spigot.proxy.common.ai.TargetGoalFactory
import net.minecraft.core.Registry
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.item.Item
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Mob
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer

private lateinit var impl: CommonsProvider

val NBT_TAG_STRING by lazy { impl.nbtTagString }

fun Mob.toPathfinderMob(): PathfinderMob? =
    impl.toPathfinderMob(this)

fun NamespacedKey.toResourceLocation(): ResourceLocation =
    impl.toResourceLocation(this)

fun ItemStack.asNMSStack(): net.minecraft.world.item.ItemStack =
    impl.asNMSStack(this)

fun net.minecraft.world.item.ItemStack.asBukkitStack(): ItemStack =
    impl.asBukkitStack(this)

fun ItemStack.mergeIfNeeded(nmsStack: net.minecraft.world.item.ItemStack) =
    impl.mergeIfNeeded(this, nmsStack)

fun LivingEntity.toBukkitEntity(): org.bukkit.entity.LivingEntity? =
    impl.toBukkitEntity(this)

fun <T : EntityGoal<*>> T.getVersionSpecificEntityGoalFactory(): EntityGoalFactory<T>? =
    impl.getVersionSpecificEntityGoalFactory(this)

fun <T : TargetGoal<*>> T.getVersionSpecificEntityGoalFactory(): TargetGoalFactory<T>? =
    impl.getVersionSpecificTargetGoalFactory(this)

private val MATERIAL_TO_ITEM = mutableMapOf<Material, Item>()

fun Material.toItem(): Item =
    MATERIAL_TO_ITEM.getOrPut(this) {
        Registry.ITEM.getOptional(this.key.toResourceLocation())
            .orElseThrow { IllegalArgumentException("Material is not item!") }
    }

fun CompoundTag.makePdc(): PersistentDataContainer =
    impl.makePdc(this)

fun CompoundTag.setPdc(pdc: PersistentDataContainer) =
    impl.setPdc(this, pdc)

interface CommonsProvider {
    val nbtTagString: Int

    fun makePdc(tag: CompoundTag): PersistentDataContainer

    fun setPdc(tag: CompoundTag, pdc: PersistentDataContainer)

    fun toPathfinderMob(mob: Mob): PathfinderMob?

    fun toResourceLocation(namespacedKey: NamespacedKey): ResourceLocation

    fun asNMSStack(itemStack: ItemStack): net.minecraft.world.item.ItemStack

    fun asBukkitStack(itemStack: net.minecraft.world.item.ItemStack): ItemStack

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
