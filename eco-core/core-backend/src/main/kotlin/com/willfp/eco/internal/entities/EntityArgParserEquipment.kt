package com.willfp.eco.internal.entities

import com.willfp.eco.core.entities.args.EntityArgParseResult
import com.willfp.eco.core.entities.args.EntityArgParser
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.EquipmentSlot

object EntityArgParserEquipment : EntityArgParser {
    override fun parseArguments(args: Array<out String>): EntityArgParseResult? {
        val equipment = mutableMapOf<EquipmentSlot, TestableItem>()

        for (arg in args) {
            for (slot in EquipmentSlot.values()) {
                if (!arg.lowercase().startsWith("${slot.name.lowercase()}:")) {
                    continue
                }
                equipment[slot] = Items.lookup(arg.substring(slot.name.length + 1, arg.length))
            }
        }

        if (equipment.isEmpty()) {
            return null
        }

        return EntityArgParseResult(
            {
                if (it !is LivingEntity) {
                    return@EntityArgParseResult false
                }

                val entityEquipment = it.equipment ?: return@EntityArgParseResult false

                for ((slot, item) in equipment) {
                    if (!item.matches(entityEquipment.getItem(slot))) {
                        return@EntityArgParseResult false
                    }
                }

                return@EntityArgParseResult true
            },
            {
                if (it !is LivingEntity) {
                    return@EntityArgParseResult
                }

                val entityEquipment = it.equipment ?: return@EntityArgParseResult

                for ((slot, item) in equipment) {
                    entityEquipment.setItem(slot, item.item, false)
                }
            }
        )
    }
}
