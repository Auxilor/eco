package com.willfp.eco.internal.items

import com.willfp.eco.internal.items.templates.FlagArgParser
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.tag.DamageTypeTags.IS_FIRE

object ArgParserFireResistant : FlagArgParser("fire_resistant") {
    override fun apply(meta: ItemMeta) {
        meta.damageResistant = IS_FIRE
    }

    override fun test(meta: ItemMeta): Boolean {
        return meta.damageResistant == IS_FIRE
    }
}
