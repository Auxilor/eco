package com.willfp.eco.internal.items

import com.willfp.eco.core.items.args.LookupArgParser
import java.util.function.Predicate
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

object ArgParserUseCooldown : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        val parts = args.firstOrNull { arg ->
            arg.startsWith("use-cooldown:", ignoreCase = true)
                    || arg.startsWith("use_cooldown:", ignoreCase = true)
        }?.run { substringAfter(":").split(":", limit = 2) }

        val seconds = parts?.firstOrNull()?.toFloatOrNull()?.takeIf { it > 0 }
        val groupArg = parts?.getOrNull(1)
        val group = groupArg?.let { NamespacedKey.fromString(it.lowercase()) }

        if (seconds == null || groupArg != null && group == null) {
            return null
        }

        meta.setUseCooldown(meta.useCooldown.apply {
            cooldownSeconds = seconds
            cooldownGroup = group
        })

        return Predicate { stack ->
            stack.itemMeta?.let { testMeta ->
                testMeta.hasUseCooldown()
                        && testMeta.useCooldown.cooldownSeconds == seconds
                        && (group == null || testMeta.useCooldown.cooldownGroup == group)
            } == true
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        if (!meta.hasUseCooldown()) {
            return null
        }

        val group = meta.useCooldown.cooldownGroup?.let { ":${it.namespace}:${it.key}" }.orEmpty()
        return "use-cooldown:${meta.useCooldown.cooldownSeconds}$group"
    }
}
