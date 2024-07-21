package com.willfp.eco.internal.items

import com.willfp.eco.core.items.args.LookupArgParser
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.Predicate

object ArgParserCustomModelData : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        val arg = args.firstOrNull {
            it.startsWith("custom-model-data:", ignoreCase = true)
                    || it.startsWith("custom_model_data:", ignoreCase = true)
        } ?: return null

        val modelData = arg.split(":")[1].toIntOrNull() ?: return null

        meta.setCustomModelData(modelData)

        return Predicate {
            val testMeta = it.itemMeta ?: return@Predicate false

            if (!testMeta.hasCustomModelData()) {
                return@Predicate false
            }

            testMeta.customModelData == modelData
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        if (!meta.hasCustomModelData()) {
            return null
        }

        return "custom_model_data:${meta.customModelData}"
    }
}
