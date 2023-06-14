package com.willfp.eco.internal.placeholder

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.Placeholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.templates.SimpleInjectablePlaceholder
import java.util.regex.Pattern

/*

A set of global placeholders that are always available.

 */
private val globalPlaceholders = setOf<Placeholder>(
    object : SimpleInjectablePlaceholder("player") {
        override fun getValue(args: String, context: PlaceholderContext): String? {
            return context.player?.name
        }
    },
)

class PlaceholderLookup(
    val args: String,
    val plugin: EcoPlugin?,
    private val injections: Collection<InjectablePlaceholder>?
) {
    fun findMatchingPlaceholder(): Placeholder? {
        if (plugin != null) {
            val pluginPlaceholders = PlaceholderManager.getRegisteredPlaceholders(plugin)
            for (placeholder in pluginPlaceholders) {
                if (placeholder.matches(this)) {
                    return placeholder
                }
            }
        }

        if (injections != null) {
            for (placeholder in injections) {
                if (placeholder.matches(this)) {
                    return placeholder
                }
            }
        }

        for (placeholder in globalPlaceholders) {
            if (placeholder.matches(this)) {
                return placeholder
            }
        }

        return null
    }

    private fun Placeholder.matches(lookup: PlaceholderLookup): Boolean {
        val pattern = this.pattern
        val patternString = pattern.pattern()

        val patternFlags = pattern.flags()
        val isLiteral = Pattern.LITERAL and patternFlags != 0

        return if (isLiteral) lookup.args == patternString else pattern.matcher(lookup.args).matches()
    }
}
