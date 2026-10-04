package com.willfp.eco.internal.items

import com.willfp.eco.internal.items.templates.FlagArgParser
import com.willfp.eco.util.NamespacedKeyUtils
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

object ArgParserUnconsumable : FlagArgParser("unconsumable") {
    val key = NamespacedKeyUtils.createEcoKey("unconsumable")

    override fun apply(meta: ItemMeta) {
        meta.persistentDataContainer.set(key, PersistentDataType.BOOLEAN, true)
    }

    override fun test(meta: ItemMeta): Boolean {
        return meta.persistentDataContainer.has(key, PersistentDataType.BOOLEAN)
    }
}
