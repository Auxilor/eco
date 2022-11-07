package com.willfp.eco.internal.spigot.recipes.listeners

import com.willfp.eco.internal.items.ArgParserUndyeable.isUndyeable
import com.willfp.eco.internal.spigot.recipes.GenericCraftEvent
import com.willfp.eco.internal.spigot.recipes.RecipeListener
import org.bukkit.inventory.meta.LeatherArmorMeta

object Undyeable : RecipeListener {
    override fun handle(event: GenericCraftEvent) {
        val item = event.inventory.matrix.firstOrNull { it != null && it.itemMeta is LeatherArmorMeta
                && it.itemMeta?.isUndyeable == true }?: return

        val result = event.inventory.result?: return
        if (result.type != item.type || result.itemMeta !is LeatherArmorMeta) return

        if ((item.itemMeta as LeatherArmorMeta).color != (result.itemMeta as LeatherArmorMeta).color) {
            event.deny()
        }
    }
}
