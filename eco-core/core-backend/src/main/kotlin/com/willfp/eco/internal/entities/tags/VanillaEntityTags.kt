package com.willfp.eco.internal.entities.tags

import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.tag.VanillaEntityTag
import org.bukkit.Keyed
import org.bukkit.Tag
import org.bukkit.entity.EntityType

object VanillaEntityTags {
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
            if (tag.isEntityType) {
                Entities.registerTag(
                    @Suppress("UNCHECKED_CAST")
                    VanillaEntityTag(tag.name, tag.tag as Tag<EntityType>)
                )
            }
        }
    }

    private data class NamedTag<T : Keyed>(val name: String, val tag: Tag<T>) {
        val isEntityType: Boolean
            get() = tag.values.firstOrNull() is EntityType
    }
}