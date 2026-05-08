package com.willfp.eco.internal.placeholder

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.Placeholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.templates.SimpleInjectablePlaceholder
import java.util.regex.Pattern

private val globalPlaceholders = setOf<Placeholder>(
    object : SimpleInjectablePlaceholder("player") {
        override fun getValue(args: String, context: PlaceholderContext): String? {
            return context.player?.name
        }
    },
)

data class PlaceholderLookup(
    val args: String,
    val plugin: EcoPlugin?,
    private val injections: Collection<InjectablePlaceholder>?
) {
    fun findMatchingPlaceholder(): Placeholder? {
        if (plugin != null) {
            val direct = PlaceholderManager.getRegisteredPlaceholder(plugin, args)
            if (direct != null) {
                return direct
            }
        }

        if (injections != null) {
            for (placeholder in injections) {
                if (placeholder.matches(args)) {
                    return placeholder
                }
            }
        }

        for (placeholder in globalPlaceholders) {
            if (placeholder.matches(args)) {
                return placeholder
            }
        }

        return null
    }

    companion object {
        private fun Placeholder.matches(args: String): Boolean {
            val pattern = this.pattern
            val patternFlags = pattern.flags()
            val isLiteral = Pattern.LITERAL and patternFlags != 0

            return if (isLiteral) args == pattern.pattern() else pattern.matcher(args).matches()
        }
    }
}
