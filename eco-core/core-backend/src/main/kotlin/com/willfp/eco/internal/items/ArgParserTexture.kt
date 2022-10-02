package com.willfp.eco.internal.items

import com.willfp.eco.core.items.args.LookupArgParser
import com.willfp.eco.util.SkullUtils
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.function.Predicate

object ArgParserTexture : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        if (meta !is SkullMeta) {
            return null
        }

        var texture: String? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("texture", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            texture = argSplit[1]
        }

        texture ?: return null

        SkullUtils.setSkullTexture(meta, texture)

        return Predicate {
            val testMeta = it.itemMeta as? SkullMeta ?: return@Predicate false

            texture == SkullUtils.getSkullTexture(testMeta)
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        if (meta !is SkullMeta) {
            return null
        }

        return "texture:${SkullUtils.getSkullTexture(meta)}"
    }
}