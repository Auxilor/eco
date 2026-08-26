package com.willfp.eco.internal.spigot.datapack

import com.google.gson.JsonSyntaxException
import com.willfp.eco.internal.spigot.proxies.DatapackCodecProxy
import java.util.logging.Logger

/**
 * Validates entries before anything is written.
 *
 * Validation is mandatory because the failure mode is not degradation, it is a server that will not
 * start: a malformed entry produces `Failed to load datapacks, can't proceed with server load`
 * and the server never reaches `Preparing level`.
 *
 * Four tiers were considered. Three are implemented:
 *
 * 1. JSON syntax.
 * 2. Path and ID shape.
 * 3. Codec decode, through the server's own codec. This is the one that matters.
 *
 * Tier 4, a dry-run registry load catching cross-references and cycles, is out of scope.
 *
 * Entries in registries with no reachable codec (the reloadable ones) get tiers 1 and 2 only. That
 * is acceptable: those are reloadable, so a bad one fails a `/reload`, not a boot.
 */
class DatapackValidator(
    private val codec: DatapackCodecProxy?,
    private val logger: Logger? = null
) {
    private val validatable: Set<String> by lazy {
        val registries = runCatching { codec?.validatableRegistries() }.getOrNull().orEmpty()

        // Guard against Mojang moving a registry between lifecycle classes. The classifier's
        // reloadable set is static, and the failure it would cause is silent: eco would report
        // "no restart needed" for content that never loads.
        val overlap = registries.filter { LifecycleClassifier.isReloadable(it) }

        if (overlap.isNotEmpty()) {
            logger?.warning(
                "Registries $overlap are loaded at boot on this server but eco treats them as " +
                        "reloadable. Datapack content in them may silently fail to apply; " +
                        "this needs an eco update."
            )
        }

        registries
    }

    /**
     * @return null if the entry is valid, else a human-readable error.
     */
    fun validate(entry: DatapackEntry): String? {
        validateShape(entry)?.let { return it }

        if (!entry.isJson) {
            return null
        }

        val json = entry.content.toString(Charsets.UTF_8)

        try {
            val parsed = JsonCanonicaliser.parseStrict(json)

            if (!parsed.isJsonObject) {
                return "$entry: top-level value must be a JSON object"
            }
        } catch (e: JsonSyntaxException) {
            return "$entry: malformed JSON (${e.message})"
        }

        return validateCodec(entry, json)
    }

    private fun validateShape(entry: DatapackEntry): String? {
        val registry = entry.registry.trim('/')

        if (registry.isEmpty()) {
            return "$entry: registry must not be empty"
        }

        if (!REGISTRY_PATTERN.matches(registry)) {
            return "$entry: invalid registry directory '$registry'"
        }

        if (!NAMESPACE_PATTERN.matches(entry.id.namespace)) {
            return "$entry: invalid namespace '${entry.id.namespace}'"
        }

        if (!KEY_PATTERN.matches(entry.id.key)) {
            return "$entry: invalid ID '${entry.id.key}'"
        }

        if (entry.id.key.split("/").any { it == "." || it == ".." || it.isEmpty() }) {
            return "$entry: ID must not contain path traversal"
        }

        return null
    }

    private fun validateCodec(entry: DatapackEntry, json: String): String? {
        val proxy = codec ?: return null
        val registry = entry.registry.trim('/').lowercase()

        if (registry !in validatable) {
            return null
        }

        val error = runCatching { proxy.validate(registry, json) }
            .getOrElse { return "$entry: codec validation threw (${it.message})" }
            ?: return null

        return "$entry: $error"
    }

    private companion object {
        val REGISTRY_PATTERN = Regex("[a-z0-9_.-]+(/[a-z0-9_.-]+)*")
        val NAMESPACE_PATTERN = Regex("[a-z0-9_.-]+")
        val KEY_PATTERN = Regex("[a-z0-9_./-]+")
    }
}
