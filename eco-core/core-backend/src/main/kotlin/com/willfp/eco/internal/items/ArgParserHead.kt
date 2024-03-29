package com.willfp.eco.internal.items

import com.willfp.eco.core.items.args.LookupArgParser
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.function.Predicate

object ArgParserHead : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        if (meta !is SkullMeta) {
            return null
        }

        var playerName: String? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("head", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            playerName = argSplit[1]
        }

        playerName ?: return null

        val player = Bukkit.getOfflinePlayer(playerName)

        meta.owningPlayer = player

        return Predicate {
            val testMeta = it.itemMeta as? SkullMeta ?: return@Predicate false
            testMeta.owningPlayer?.uniqueId == player.uniqueId
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        if (meta !is SkullMeta) {
            return null
        }

        if (meta.owningPlayer == null) {
            return null
        }

        if (meta.owningPlayer!!.name.equals("null", true) || meta.owningPlayer!!.name == null) {
            return null
        }

        return "head:${meta.owningPlayer?.name}"
    }
}
