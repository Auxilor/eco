package com.willfp.eco.internal.spigot.proxy.v1_18_R1

import com.willfp.eco.internal.spigot.proxy.CommonsInitializerProxy
import com.willfp.eco.internal.spigot.proxy.common.CommonsProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.PathfinderMob
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.v1_18_R1.CraftServer
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftMob
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_18_R1.util.CraftMagicNumbers
import org.bukkit.craftbukkit.v1_18_R1.util.CraftNamespacedKey
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Field

class CommonsInitializer : CommonsInitializerProxy {
    override fun init() {
        CommonsProvider.setIfNeeded(CommonsProviderImpl)
    }

    object CommonsProviderImpl : CommonsProvider {
        private val cisHandle: Field = CraftItemStack::class.java.getDeclaredField("handle").apply {
            isAccessible = true
        }

        override val nbtTagString = CraftMagicNumbers.NBT.TAG_STRING

        override fun toPathfinderMob(mob: Mob): PathfinderMob? {
            val craft = mob as? CraftMob ?: return null
            return craft.handle as? PathfinderMob
        }

        override fun toResourceLocation(namespacedKey: NamespacedKey): ResourceLocation =
            CraftNamespacedKey.toMinecraft(namespacedKey)

        override fun asNMSStack(itemStack: ItemStack): net.minecraft.world.item.ItemStack {
            return if (itemStack !is CraftItemStack) {
                CraftItemStack.asNMSCopy(itemStack)
            } else {
                cisHandle[itemStack] as net.minecraft.world.item.ItemStack? ?: CraftItemStack.asNMSCopy(itemStack)
            }
        }

        override fun asBukkitStack(itemStack: net.minecraft.world.item.ItemStack): ItemStack {
            return CraftItemStack.asBukkitCopy(itemStack)
        }

        override fun mergeIfNeeded(itemStack: ItemStack, nmsStack: net.minecraft.world.item.ItemStack) {
            if (itemStack !is CraftItemStack) {
                itemStack.itemMeta = CraftItemStack.asCraftMirror(nmsStack).itemMeta
            }
        }

        override fun toBukkitEntity(entity: net.minecraft.world.entity.LivingEntity): LivingEntity? =
            CraftEntity.getEntity(Bukkit.getServer() as CraftServer, entity) as? LivingEntity
    }
}
