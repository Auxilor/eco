package com.willfp.eco.internal.spigot.proxy.v1_19_R2

import com.willfp.eco.internal.spigot.proxy.CommonsInitializerProxy
import com.willfp.eco.internal.spigot.proxy.common.CommonsProvider
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.PathfinderMob
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.v1_19_R2.CraftServer
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftEntity
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftMob
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataContainer
import org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataTypeRegistry
import org.bukkit.craftbukkit.v1_19_R2.util.CraftMagicNumbers
import org.bukkit.craftbukkit.v1_19_R2.util.CraftNamespacedKey
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import java.lang.reflect.Field

class CommonsInitializer : CommonsInitializerProxy {
    override fun init() {
        CommonsProvider.setIfNeeded(CommonsProviderImpl)
    }

    object CommonsProviderImpl : CommonsProvider {
        private val cisHandle: Field = CraftItemStack::class.java.getDeclaredField("handle").apply {
            isAccessible = true
        }

        private val pdcRegsitry = Class.forName("org.bukkit.craftbukkit.v1_19_R2.inventory.CraftMetaItem")
            .getDeclaredField("DATA_TYPE_REGISTRY")
            .apply { isAccessible = true }
            .get(null) as CraftPersistentDataTypeRegistry

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

        override fun makePdc(tag: CompoundTag, base: Boolean): PersistentDataContainer {
            fun emptyPdc(): CraftPersistentDataContainer = CraftPersistentDataContainer(pdcRegsitry)

            fun CompoundTag?.toPdc(): PersistentDataContainer {
                val pdc = emptyPdc()
                this ?: return pdc
                val keys = this.allKeys
                for (key in keys) {
                    pdc.put(key, this[key])
                }

                return pdc
            }

            return if (base) {
                tag.toPdc()
            } else {
                if (tag.contains("PublicBukkitValues")) {
                    tag.getCompound("PublicBukkitValues").toPdc()
                } else {
                    emptyPdc()
                }
            }
        }

        override fun setPdc(
            tag: CompoundTag,
            pdc: PersistentDataContainer?,
            item: net.minecraft.world.item.ItemStack?
        ) {
            fun CraftPersistentDataContainer.toTag(): CompoundTag {
                val compound = CompoundTag()
                val rawPublicMap: Map<String, Tag> = this.raw
                for ((key, value) in rawPublicMap) {
                    compound.put(key, value)
                }

                return compound
            }

            val container = when (pdc) {
                is CraftPersistentDataContainer? -> pdc
                else -> null
            }

            if (item != null) {
                if (container != null && !container.isEmpty) {
                    for (key in tag.allKeys.toSet()) {
                        tag.remove(key)
                    }

                    tag.merge(container.toTag())
                } else {
                    item.setTag(null)
                }
            } else {
                if (container != null && !container.isEmpty) {
                    tag.put("PublicBukkitValues", container.toTag())
                } else {
                    tag.remove("PublicBukkitValues")
                }
            }
        }
    }
}
