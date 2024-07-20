package com.willfp.eco.internal.items.tags

import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.tag.VanillaItemTag
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.Tag

object VanillaItemTags {
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
                Items.registerTag(
                    @Suppress("UNCHECKED_CAST")
                    VanillaItemTag(tag.name, tag.tag as Tag<Material>)
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
