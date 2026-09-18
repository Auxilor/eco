@file:JvmName("ComponentValues")

package com.willfp.eco.core.items

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.willfp.eco.util.StringUtils

/**
 * Convert formatted (section sign) text into a plain component value, for use
 * with text components such as `minecraft:item_name` and `minecraft:custom_name`.
 *
 * Text components have to be passed as components rather than as strings: a
 * bare string is parsed as a literal, and when the client renders one it only
 * understands the sixteen legacy codes. Hex colours and gradients format into
 * §x sequences, which it skips over, leaving one legacy colour per character.
 *
 * @param legacy The formatted text.
 * @return The plain component value.
 */
fun legacyToComponentValue(legacy: String): Any? =
    JsonParser.parseString(StringUtils.legacyToJson(legacy)).toPlainValue()

private fun JsonElement.toPlainValue(): Any? = when {
    isJsonNull -> null
    isJsonObject -> asJsonObject.entrySet().associate { (key, value) -> key to value.toPlainValue() }
    isJsonArray -> asJsonArray.map { it.toPlainValue() }
    else -> asJsonPrimitive.let {
        when {
            it.isBoolean -> it.asBoolean
            it.isNumber -> it.asDouble
            else -> it.asString
        }
    }
}
