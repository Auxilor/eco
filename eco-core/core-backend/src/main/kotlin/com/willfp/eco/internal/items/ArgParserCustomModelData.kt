package com.willfp.eco.internal.items

import com.willfp.eco.core.items.args.LookupArgParser
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.Predicate

object ArgParserCustomModelData : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        var modelData: Int? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("custom-model-data", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            modelData = argSplit[1].toIntOrNull()
        }

        modelData ?: return null

        meta.setCustomModelData(modelData)

        return Predicate {
            val testMeta = it.itemMeta ?: return@Predicate false

            testMeta.customModelData == modelData
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        if (!meta.hasCustomModelData()) {
            return null
        }

        return "custom-model-data:${meta.customModelData}"
    }
}
