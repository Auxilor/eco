package com.willfp.eco.internal.spigot.datapack

import com.willfp.eco.core.datapack.DatapackDraft
import org.bukkit.NamespacedKey

/**
 * One file in a pack.
 *
 * @param registry The directory under `data/<namespace>/`, e.g. `worldgen/biome`.
 * @param id       The entry ID.
 * @param content  The raw content, as handed over.
 * @param isText   Whether the content came in as text and can be canonicalised.
 */
class DatapackEntry(
    val registry: String,
    val id: NamespacedKey,
    val content: ByteArray,
    val isText: Boolean
) {
    /** The path of this entry within the pack, using `/` separators. */
    val path: String = "data/${id.namespace}/${registry.trim('/')}/${fileName(registry, id, isText)}"

    /** Whether this entry's content is JSON, and so subject to canonicalisation and codec checks. */
    val isJson: Boolean = path.endsWith(".json")

    /**
     * How another entry refers to this one: `namespace:key`, with any known file extension
     * dropped. A reference names the entry, not the file it happens to live in.
     */
    val referenceId: String = "${id.namespace}:${withoutExtension(id.key)}"

    override fun toString() = "$registry/${id.namespace}:${id.key}"

    companion object {
        private val KNOWN_EXTENSIONS = setOf(".json", ".nbt", ".mcfunction", ".snbt")

        private fun withoutExtension(key: String): String {
            val extension = KNOWN_EXTENSIONS.firstOrNull { key.endsWith(it) } ?: return key

            return key.removeSuffix(extension)
        }

        private fun fileName(registry: String, id: NamespacedKey, isText: Boolean): String {
            val key = id.key

            if (KNOWN_EXTENSIONS.any { key.endsWith(it) }) {
                return key
            }

            // Functions are mcfunction source, not JSON, and are the one text registry that isn't.
            if (registry.trim('/').lowercase() == "function") {
                return "$key.mcfunction"
            }

            return if (isText) "$key.json" else "$key.nbt"
        }
    }
}

/**
 * Accumulates entries before publish.
 *
 * Not thread-safe: a draft is built and consumed inside a single [com.willfp.eco.core.datapack.DatapackHandle.apply] call.
 */
class EcoDatapackDraft : DatapackDraft {
    private val backing = mutableListOf<DatapackEntry>()

    /** The entries added so far, in insertion order. */
    val entries: List<DatapackEntry>
        get() = backing.toList()

    override fun put(registry: String, id: NamespacedKey, content: String): DatapackDraft =
        put(registry, id, content.toByteArray(Charsets.UTF_8), isText = true)

    override fun put(registry: String, id: NamespacedKey, content: ByteArray): DatapackDraft =
        put(registry, id, content.copyOf(), isText = false)

    private fun put(registry: String, id: NamespacedKey, content: ByteArray, isText: Boolean): DatapackDraft {
        backing.add(DatapackEntry(registry, id, content, isText))
        return this
    }
}
