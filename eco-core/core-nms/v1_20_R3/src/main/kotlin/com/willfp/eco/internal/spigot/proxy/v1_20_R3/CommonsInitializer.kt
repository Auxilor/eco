package com.willfp.eco.internal.spigot.proxy.v1_20_R3

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.internal.spigot.proxy.CommonsInitializerProxy
import com.willfp.eco.internal.spigot.proxy.common.CommonsProvider
import com.willfp.eco.internal.spigot.proxy.common.packet.PacketInjectorListener
import com.willfp.eco.internal.spigot.proxy.common.toResourceLocation
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.item.Item
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.v1_20_R3.CraftServer
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftMob
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_20_R3.persistence.CraftPersistentDataContainer
import org.bukkit.craftbukkit.v1_20_R3.persistence.CraftPersistentDataTypeRegistry
import org.bukkit.craftbukkit.v1_20_R3.util.CraftMagicNumbers
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import java.lang.reflect.Field

class CommonsInitializer : CommonsInitializerProxy {
    override fun init(plugin: EcoPlugin) {
        CommonsProvider.setIfNeeded(CommonsProviderImpl)
        plugin.onEnable {
            plugin.eventManager.registerListener(PacketInjectorListener)
        }
    }

    object CommonsProviderImpl : CommonsProvider {
        private val cisHandle: Field = CraftItemStack::class.java.getDeclaredField("handle").apply {
            isAccessible = true
        }

        private val pdcRegsitry = Class.forName("org.bukkit.craftbukkit.v1_20_R3.inventory.CraftMetaItem")
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
            return CraftItemStack.asCraftMirror(itemStack)
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
                    item.tag = null
                }
            } else {
                if (container != null && !container.isEmpty) {
                    tag.put("PublicBukkitValues", container.toTag())
                } else {
                    tag.remove("PublicBukkitValues")
                }
            }
        }

        override fun materialToItem(material: Material): Item =
            BuiltInRegistries.ITEM.getOptional(material.key.toResourceLocation())
                .orElseThrow { IllegalArgumentException("Material is not item!") }

        override fun itemToMaterial(item: Item) =
            Material.getMaterial(BuiltInRegistries.ITEM.getKey(item).path.uppercase())
                ?: throw IllegalArgumentException("Invalid material!")

        override fun toNMS(player: Player): ServerPlayer {
            return (player as CraftPlayer).handle
        }

        override fun toNMS(component: Component): net.minecraft.network.chat.Component {
            val json = JSONComponentSerializer.json().serialize(component)
            return net.minecraft.network.chat.Component.Serializer.fromJson(json)!!
        }
    }
}
