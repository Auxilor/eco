package com.willfp.eco.internal.spigot.proxy.v1_19_R3

import com.willfp.eco.core.tuples.Pair
import com.willfp.eco.internal.spigot.proxy.ItemMetaProxy
import org.bukkit.inventory.meta.ItemMeta

class ItemMeta : ItemMetaProxy {
    override fun getTrim(meta: ItemMeta): Pair<String, String>? {
        return null
    }

    override fun setTrim(meta: ItemMeta, trim: Pair<String, String>) {}
}