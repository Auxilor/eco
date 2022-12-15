package com.willfp.eco.internal.spigot.proxy.v1_19_R2

import com.willfp.eco.core.data.ExtendedPersistentDataContainer
import com.willfp.eco.internal.spigot.proxy.ExtendedPersistentDataContainerFactoryProxy
import net.minecraft.nbt.Tag
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataContainer
import org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataTypeRegistry
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

class ExtendedPersistentDataContainerFactory : ExtendedPersistentDataContainerFactoryProxy {
    @Suppress("UNCHECKED_CAST")
    private val registry: CraftPersistentDataTypeRegistry

    init {
        /*
         Can't grab actual instance since it's in CraftMetaItem (which is package-private)
         And getting it would mean more janky reflection
         */
        val item = CraftItemStack.asCraftCopy(ItemStack(Material.STONE))
        val pdc = item.itemMeta!!.persistentDataContainer
        this.registry = CraftPersistentDataContainer::class.java.getDeclaredField("registry")
            .apply { isAccessible = true }.get(pdc) as CraftPersistentDataTypeRegistry
    }

    override fun adapt(pdc: PersistentDataContainer): ExtendedPersistentDataContainer {
        return when (pdc) {
            is CraftPersistentDataContainer -> EcoPersistentDataContainer(pdc)
            else -> throw IllegalArgumentException("Custom PDC instance ims not supported!")
        }
    }

    override fun newPdc(): PersistentDataContainer {
        return CraftPersistentDataContainer(registry)
    }

    inner class EcoPersistentDataContainer(
        val handle: CraftPersistentDataContainer
    ) : ExtendedPersistentDataContainer {
        @Suppress("UNCHECKED_CAST")
        private val customDataTags: MutableMap<String, Tag> =
            CraftPersistentDataContainer::class.java.getDeclaredField("customDataTags")
                .apply { isAccessible = true }.get(handle) as MutableMap<String, Tag>

        override fun <T : Any, Z : Any> set(key: String, dataType: PersistentDataType<T, Z>, value: Z) {
            customDataTags[key] =
                registry.wrap(dataType.primitiveType, dataType.toPrimitive(value, handle.adapterContext))
        }

        override fun <T : Any, Z : Any> has(key: String, dataType: PersistentDataType<T, Z>): Boolean {
            val value = customDataTags[key] ?: return false
            return registry.isInstanceOf(dataType.primitiveType, value)
        }

        override fun <T : Any, Z : Any> get(key: String, dataType: PersistentDataType<T, Z>): Z? {
            val value = customDataTags[key] ?: return null
            return dataType.fromPrimitive(registry.extract(dataType.primitiveType, value), handle.adapterContext)
        }

        override fun <T : Any, Z : Any> getOrDefault(
            key: String,
            dataType: PersistentDataType<T, Z>,
            defaultValue: Z
        ): Z {
            return get(key, dataType) ?: defaultValue
        }

        override fun remove(key: String) {
            customDataTags.remove(key)
        }

        override fun getAllKeys(): MutableSet<String> {
            return customDataTags.keys
        }

        override fun getBase(): PersistentDataContainer {
            return handle
        }
    }
}
