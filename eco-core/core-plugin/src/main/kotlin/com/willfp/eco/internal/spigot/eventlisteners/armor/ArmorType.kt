package com.willfp.eco.internal.spigot.eventlisteners.armor
import org.bukkit.inventory.ItemStack

enum class ArmorType(val slot: Int) {
    HELMET(5), CHESTPLATE(6), LEGGINGS(7), BOOTS(8);

    companion object {
        fun matchType(itemStack: ItemStack?): ArmorType? {
            if (ArmorListener.isAirOrNull(itemStack)) {
                return null
            }
            itemStack ?: return null
            val type = itemStack.type.name
            return if (type.endsWith("_HELMET") || type.endsWith("_SKULL") || type.endsWith("PLAYER_HEAD")) {
                HELMET
            } else if (type.endsWith("_CHESTPLATE") || type.endsWith("ELYTRA")) {
                CHESTPLATE
            } else if (type.endsWith("_LEGGINGS")) {
                LEGGINGS
            } else if (type.endsWith("_BOOTS")) {
                BOOTS
            } else {
                null
            }
        }
    }
}