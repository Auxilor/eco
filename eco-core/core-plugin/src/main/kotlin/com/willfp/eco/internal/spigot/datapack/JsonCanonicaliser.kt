package com.willfp.eco.internal.spigot.datapack

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import java.io.StringReader

/**
 * Produces deterministic bytes for JSON datapack entries.
 *
 * Determinism is eco's responsibility, not the consumer's: a plugin handing over a JSON string
 * should not have to care about key order or whitespace. Two semantically identical drafts must
 * produce identical bytes, otherwise change detection reports a change that isn't one and the
 * operator gets a spurious restart prompt.
 */
object JsonCanonicaliser {
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .serializeNulls()
        .create()

    /**
     * Parse [json] strictly, rejecting anything Minecraft's own loader would.
     *
     * @throws JsonSyntaxException If the content is not a single, well-formed JSON value.
     */
    fun parseStrict(json: String): JsonElement {
        // Gson#fromJson forces leniency on the reader it is given, so syntax is checked separately.
        JsonReader(StringReader(json)).use { reader ->
            @Suppress("DEPRECATION")
            reader.isLenient = false

            try {
                reader.skipValue()

                if (reader.peek() != JsonToken.END_DOCUMENT) {
                    throw JsonSyntaxException("Trailing content after the top-level value")
                }
            } catch (e: IllegalStateException) {
                throw JsonSyntaxException(e.message ?: "Malformed JSON", e)
            } catch (e: java.io.IOException) {
                throw JsonSyntaxException(e.message ?: "Malformed JSON", e)
            }
        }

        return JsonParser.parseString(json)
    }

    /**
     * Sort object keys and apply fixed formatting, so equivalent content is byte-identical.
     *
     * Array order is preserved: arrays are semantically ordered in datapack schemas.
     */
    fun canonicalise(json: String): String = gson.toJson(sort(parseStrict(json))) + "\n"

    private fun sort(element: JsonElement): JsonElement = when (element) {
        is JsonObject -> JsonObject().apply {
            for (key in element.keySet().sorted()) {
                add(key, sort(element.get(key)))
            }
        }

        is JsonArray -> JsonArray().apply {
            for (child in element) {
                add(sort(child))
            }
        }

        else -> element
    }
}
