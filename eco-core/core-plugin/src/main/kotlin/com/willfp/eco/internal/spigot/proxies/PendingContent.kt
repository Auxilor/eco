package com.willfp.eco.internal.spigot.proxies

import com.willfp.eco.internal.spigot.datapack.DatapackEntry

/**
 * The entries a publish is about to write, so that codec validation can resolve references to
 * them.
 *
 * Codec decode resolves some holders eagerly against the live registries — a placed feature's
 * `feature`, a structure set's `structure`, a structure's `biomes` — and the live registries
 * cannot contain content this publish has not written yet. Without this, a pack whose entries
 * reference each other can never be published: the reference resolves only once its target is
 * live, the target goes live only after a restart, and the restart only follows a successful
 * write.
 *
 * A reference to something in neither the live registries nor the draft still fails, which is the
 * part worth keeping.
 *
 * @param elements Registry directory (`worldgen/biome`) to the IDs the draft writes there.
 * @param tags     Registry directory the tags apply to, to the tag IDs the draft writes.
 */
class PendingContent(
    val elements: Map<String, Set<String>>,
    val tags: Map<String, Set<String>>
) {
    /** Whether this publish adds nothing that validation needs to know about. */
    fun isEmpty() = elements.isEmpty() && tags.isEmpty()

    /** The IDs written to [registryDirPath] by this publish. */
    fun elementsIn(registryDirPath: String): Set<String> = elements[registryDirPath].orEmpty()

    /** The tag IDs written for [registryDirPath] by this publish. */
    fun tagsIn(registryDirPath: String): Set<String> = tags[registryDirPath].orEmpty()

    companion object {
        val EMPTY = PendingContent(emptyMap(), emptyMap())

        /**
         * Splits a draft into the elements and tags it writes.
         *
         * One implementation, so that the publish path and the tests agree on what a draft
         * contributes rather than each deciding for itself.
         */
        fun of(entries: Collection<DatapackEntry>): PendingContent {
            val elements = mutableMapOf<String, MutableSet<String>>()
            val tags = mutableMapOf<String, MutableSet<String>>()

            for (entry in entries) {
                val registry = entry.registry.trim('/').lowercase()

                if (registry.startsWith("tags/")) {
                    tags.getOrPut(registry.removePrefix("tags/")) { mutableSetOf() }.add(entry.referenceId)
                } else {
                    elements.getOrPut(registry) { mutableSetOf() }.add(entry.referenceId)
                }
            }

            return PendingContent(elements, tags)
        }
    }
}
