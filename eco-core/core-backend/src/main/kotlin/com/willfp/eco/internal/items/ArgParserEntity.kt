package com.willfp.eco.internal.items

import com.willfp.eco.core.items.args.LookupArgParser
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.Predicate

object ArgParserEntity : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        if (meta !is BlockStateMeta) {
            return null
        }

        if (meta.hasBlockState() || meta.blockState !is CreatureSpawner) {
            return null
        }

        val state = meta.blockState as CreatureSpawner

        var type: String? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("entity", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            type = argSplit[1]
        }

        type ?: return null

        val entityType = runCatching { EntityType.valueOf(type.uppercase()) }.getOrNull() ?: return null

        state.spawnedType = entityType

        meta.blockState = state

        return Predicate {
            val testMeta = ((it.itemMeta as? BlockStateMeta) as? CreatureSpawner) ?: return@Predicate false

            testMeta.spawnedType?.name?.equals(type, true) == true
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        if (meta !is BlockStateMeta) {
            return null
        }

        if (meta.hasBlockState() || meta.blockState !is CreatureSpawner) {
            return null
        }

        val state = meta.blockState as CreatureSpawner

        return state.spawnedType?.let { "entity:${state.spawnedType!!.name}" }
    }
}
