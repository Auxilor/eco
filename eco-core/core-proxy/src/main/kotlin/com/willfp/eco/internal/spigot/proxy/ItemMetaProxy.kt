package com.willfp.eco.internal.spigot.proxy

import org.bukkit.inventory.meta.ItemMeta
import com.willfp.eco.core.tuples.Pair

interface ItemMetaProxy {
    fun getTrim(meta: ItemMeta): Pair<String, String>?
    fun setTrim(meta: ItemMeta, trim: Pair<String, String>)
}