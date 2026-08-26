package com.willfp.eco.internal.spigot.datapack

/**
 * Decides whether a registry's content applies on `/reload` or only at server boot.
 *
 * The reloadable set is static, seven entries, and identical on every supported version. Everything
 * else requires a restart.
 *
 * Runtime discovery was considered and rejected: it cannot see `recipe`, `advancement`, `function`
 * or `tags` (separate loaders, private directory fields), it drifts across versions, and it fails
 * in the wrong direction. An unrecognised registry defaulting to bootstrap produces a needless
 * restart prompt, which is visible and harmless. Runtime discovery failing the other way would
 * report "no restart needed" while the content silently never loads.
 */
object LifecycleClassifier {
    /**
     * Registries whose content is picked up by a datapack reload.
     *
     * `LootDataType` (loot_table, predicate, item_modifier) plus `ReloadableServerResources`
     * (recipe, advancement, function, tags).
     */
    val RELOADABLE: Set<String> = setOf(
        "recipe",
        "advancement",
        "function",
        "tags",
        "loot_table",
        "predicate",
        "item_modifier"
    )

    /**
     * Whether entries in [registry] are applied by a datapack reload rather than at boot.
     */
    fun isReloadable(registry: String): Boolean {
        val normalised = normalise(registry)

        if (normalised in RELOADABLE) {
            return true
        }

        // tags/block, tags/item, and so on.
        return RELOADABLE.any { normalised.startsWith("$it/") }
    }

    /**
     * Whether any of [registries] holds content that only resolves at server boot.
     */
    fun requiresRestart(registries: Collection<String>) = registries.any { !isReloadable(it) }

    private fun normalise(registry: String) = registry.trim('/').lowercase()
}
