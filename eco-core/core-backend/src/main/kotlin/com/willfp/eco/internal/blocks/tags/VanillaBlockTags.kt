package com.willfp.eco.internal.blocks.tags

import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.blocks.tag.VanillaBlockTag
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.Tag

object VanillaBlockTags {
    fun register() {
        // Get all tags
        val allTags = Tag::class.java.declaredFields
            .filter { it.type == Tag::class.java }
            .mapNotNull {
                val tag = it.get(null) as? Tag<*>
                if (tag == null) {
                    null
                } else {
                    NamedTag(it.name.lowercase(), tag)
                }
            }

        // Register all tags
        for (tag in allTags) {
            if (tag.isMaterial) {
                Blocks.registerTag(
                    @Suppress("UNCHECKED_CAST")
                    (VanillaBlockTag(tag.name, tag.tag as Tag<Material>))
                )
            }
        }
    }

    private data class NamedTag<T : Keyed>(val name: String, val tag: Tag<T>) {
        // Check if tag is material
        val isMaterial: Boolean
            get() = tag.values.firstOrNull() is Material
    }
}
