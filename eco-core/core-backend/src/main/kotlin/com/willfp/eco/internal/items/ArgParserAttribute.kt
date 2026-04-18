package com.willfp.eco.internal.items

import com.willfp.eco.core.items.args.LookupArgParser
import com.willfp.eco.util.namespacedKeyOf
import org.bukkit.Registry
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.Predicate

object ArgParserAttribute : LookupArgParser {
    override fun parseArguments(args: Array<out String>, meta: ItemMeta): Predicate<ItemStack>? {
        val attributes = mutableMapOf<Attribute, MutableList<AttributeModifier>>()

        for (arg in args.filter { it.startsWith("attribute:") }) {
            try {
                val argSplit = arg.split(":")

                val attributeString = argSplit.getOrNull(1) ?: continue

                val operationString = argSplit.getOrNull(2) ?: continue

                val amountString = argSplit.getOrNull(3) ?: continue

                val slotGroupString = argSplit.getOrNull(4)

                val attribute = Registry.ATTRIBUTE.get(namespacedKeyOf("minecraft", attributeString)) ?: continue

                val operation = when (operationString) {
                    "add_number" -> AttributeModifier.Operation.ADD_NUMBER
                    "add_scalar" -> AttributeModifier.Operation.ADD_SCALAR
                    "multiply_scalar_1" -> AttributeModifier.Operation.MULTIPLY_SCALAR_1
                    else -> continue
                }

                val amount = amountString.toDoubleOrNull() ?: continue

                val slotGroup = if (slotGroupString != null) {
                    EquipmentSlotGroup.getByName(slotGroupString)
                } else {
                    null
                }

                val modifier_key = "${attributeString}__${operationString}__${amountString}__${slotGroupString ?: "null"}"

                val modifier = if (slotGroup != null) {
                    AttributeModifier(
                        namespacedKeyOf("eco", modifier_key),
                        amount,
                        operation,
                        slotGroup
                    )
                } else {
                    AttributeModifier(
                        namespacedKeyOf("eco", modifier_key),
                        amount,
                        operation
                    )
                }

                attributes.computeIfAbsent(attribute) { mutableListOf() }.add(modifier)

                if (attributes.isEmpty()) {
                    return null
                }

                for ((attribute, modifiers) in attributes) {
                    for (modifier in modifiers) {
                        meta.addAttributeModifier(attribute, modifier)
                    }
                }
            } catch (e: IllegalArgumentException) {
                continue
            }
        }

        return Predicate {
            for ((attribute, modifiers) in attributes) {
                val itemModifiers = it.itemMeta?.getAttributeModifiers(attribute) ?: continue

                if (!itemModifiers.containsAll(modifiers)) {
                    return@Predicate false
                }
            }

            true
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        val attributes = mutableMapOf<Attribute, MutableList<AttributeModifier>>()

        for (attribute in Registry.ATTRIBUTE.stream()) {
            val modifiers = meta.getAttributeModifiers(attribute) ?: continue

            if (modifiers.isEmpty()) {
                continue
            }

            attributes[attribute] = modifiers.filter { it.key.namespace == "eco" }.toMutableList()
        }

        if (attributes.isEmpty()) {
            return null
        }

        val builder = StringBuilder()

        for ((attribute, modifiers) in attributes) {
            for (modifier in modifiers) {
                val operationString = when (modifier.operation) {
                    AttributeModifier.Operation.ADD_NUMBER -> "add_number"
                    AttributeModifier.Operation.ADD_SCALAR -> "add_scalar"
                    AttributeModifier.Operation.MULTIPLY_SCALAR_1 -> "multiply_scalar_1"
                }

                val slotGroupString = modifier.slotGroup.toString().lowercase()

                builder.append("attribute:${attribute.key.key}:$operationString:${modifier.amount}:$slotGroupString ")
            }
        }

        return builder.toString().trimEnd()
    }
}