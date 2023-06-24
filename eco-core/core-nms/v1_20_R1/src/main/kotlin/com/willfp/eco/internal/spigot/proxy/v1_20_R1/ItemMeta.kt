package com.willfp.eco.internal.spigot.proxy.v1_20_R1

import com.willfp.eco.core.tuples.Pair
import com.willfp.eco.internal.spigot.proxy.ItemMetaProxy
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.inventory.meta.ArmorMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.trim.ArmorTrim

class ItemMeta : ItemMetaProxy {
    override fun getTrim(meta: ItemMeta): Pair<String, String>? {
        if (meta !is ArmorMeta) return null
        val trim = meta.trim ?: return null
        return Pair(
            trim.material.key.key,
            trim.pattern.key.key
        )
    }

    override fun setTrim(meta: ItemMeta, trim: Pair<String, String>) {
        if (meta !is ArmorMeta) return
        val material = Registry.TRIM_MATERIAL.get(
            NamespacedKey.minecraft(trim.first!!.lowercase())
        ) ?: return

        val pattern = Registry.TRIM_PATTERN.get(
            NamespacedKey.minecraft(trim.second!!.lowercase())
        ) ?: return

        meta.trim = ArmorTrim(
            material,
            pattern
        )
    }
}