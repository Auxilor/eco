package com.willfp.eco.internal.compat.modern.items.parsers

import com.willfp.eco.internal.items.templates.ValueArgParser
import com.willfp.eco.util.StringUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.inventory.meta.ItemMeta

object ArgParserItemName : ValueArgParser<Component>("item_name") {
    override fun parse(arg: String): Component {
        return StringUtils.formatToComponent(arg)
    }

    override fun apply(meta: ItemMeta, value: Component) {
        meta.itemName(value)
    }

    override fun test(meta: ItemMeta): String? {
        if (!meta.hasItemName()) {
            return null
        }

        val name = MiniMessage.miniMessage().serialize(meta.itemName())

        return name
    }
}
