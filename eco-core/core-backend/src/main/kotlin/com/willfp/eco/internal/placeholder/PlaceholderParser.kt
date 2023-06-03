package com.willfp.eco.internal.placeholder

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.Placeholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.util.StringUtils
import java.util.concurrent.TimeUnit

/*

A lot of methods here are centered around minimising calls to getPlaceholderInjections,
which tends to be slow for things like configs. This was optimised with ListViewOfCollection,
but it's still best to minimise the memory overhead.

 */

class PlaceholderParser {
    private val placeholderRegex = Regex("%([^% ]+)%")

    private val placeholderLookupCache = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.SECONDS)
        .build<PlaceholderLookup, Placeholder?>()

    fun translatePlacholders(text: String, context: PlaceholderContext): String {
        return translatePlacholders(text, context, context.injectableContext.placeholderInjections)
    }

    private fun translatePlacholders(
        text: String,
        context: PlaceholderContext,
        injections: Collection<InjectablePlaceholder>,
        translateEcoPlaceholders: Boolean = true
    ): String {
        /*

        Why am I doing injections at the start, and again at the end?

        Additional players let you use something like victim as a player to parse in relation to,
        for example doing %victim_player_health%, which would parse the health of the victim.

        However, something like libreforge will also inject %victim_max_health%, which is unrelated
        to additional players, and instead holds a constant value. So, eco saw this, smartly thought
        "ah, it's an additional player, let's parse it", and then tried to parse %max_health% with
        relation to the victim, which resolved to zero. So, we have to parse statics and player statics
        that might include a prefix first, then additional players, then player statics with the support
        of additional players.

        This was a massive headache and took so many reports before I clocked what was going on.

        Oh well, at least it's fixed now.

         */

        // Apply injections first
        var processed = injections.fold(text) { acc, injection ->
            injection.tryTranslateQuickly(acc, context)
        }

        // Prevent running 2 scans if there are no additional players.
        if (context.additionalPlayers.isNotEmpty()) {
            val found = PlaceholderManager.findPlaceholdersIn(text)
            for (additionalPlayer in context.additionalPlayers) {
                val prefix = "%${additionalPlayer.identifier}_"
                processed = found.fold(processed) { acc, placeholder ->
                    if (placeholder.startsWith(prefix)) {
                        val newPlaceholder = "%${StringUtils.removePrefix(prefix, placeholder)}"
                        val translation = translatePlacholders(
                            newPlaceholder,
                            context.copyWithPlayer(additionalPlayer.player),
                            injections
                        )
                        acc.replace(placeholder, translation)
                    } else {
                        acc
                    }
                }
            }
        }

        // Translate eco placeholders
        if (translateEcoPlaceholders) {
            processed = translateEcoPlaceholdersIn(processed, context, injections)
        }

        // Apply registered integrations
        processed = PlaceholderManager.getRegisteredIntegrations().fold(processed) { acc, integration ->
            integration.translate(acc, context.player)
        }

        // Apply injections again
        return injections.fold(processed) { acc, injection ->
            injection.tryTranslateQuickly(acc, context)
        }
    }

    fun getPlaceholderResult(
        plugin: EcoPlugin?,
        args: String,
        context: PlaceholderContext
    ): String? {
        // Only scan for injections if plugin is null.
        val injections = if (plugin == null) context.injectableContext.placeholderInjections else null

        return doGetResult(plugin, args, injections, context)
    }

    // Injections are sent separately here to prevent multiple calls to getPlaceholderInjections
    private fun doGetResult(
        plugin: EcoPlugin?,
        args: String?,
        injections: Collection<InjectablePlaceholder>?,
        context: PlaceholderContext
    ): String? {
        if (args == null) {
            return null
        }

        val lookup = PlaceholderLookup(args, plugin, injections)

        val placeholder = placeholderLookupCache.get(lookup) {
            it.findMatchingPlaceholder()
        }

        return placeholder?.getValue(args, context)
    }

    private fun translateEcoPlaceholdersIn(
        text: String,
        context: PlaceholderContext,
        injections: Collection<InjectablePlaceholder>
    ): String {
        val output = StringBuilder()
        var lastAppendPosition = 0

        for (matchResult in placeholderRegex.findAll(text)) {
            val placeholder = matchResult.groups[1]?.value ?: ""

            val injectableResult = doGetResult(null, placeholder, injections, context)

            val parts = placeholder.split("_", limit = 2)

            var result: String? = null

            if (injectableResult != null) {
                result = injectableResult
            } else if (parts.size == 2) {
                val plugin = EcoPlugin.getPlugin(parts[0])

                if (plugin != null) {
                    result = doGetResult(plugin, parts[1], null, context)
                }
            }

            output.append(text.substring(lastAppendPosition, matchResult.range.first))

            output.append(result ?: matchResult.value)

            lastAppendPosition = matchResult.range.last + 1
        }

        output.append(text.substring(lastAppendPosition))
        return output.toString()
    }

    fun parseIndividualPlaceholders(strings: Collection<String>, context: PlaceholderContext): Collection<String> {
        val injections = context.injectableContext.placeholderInjections

        return strings.map {
            parseIndividualEcoPlaceholder(it, context, injections)
                ?: translatePlacholders(
                    it,
                    context,
                    injections,
                    translateEcoPlaceholders = false
                ) // Default to slower translation
        }
    }

    private fun parseIndividualEcoPlaceholder(
        string: String,
        context: PlaceholderContext,
        injections: Collection<InjectablePlaceholder>
    ): String? {
        val placeholder = string.substring(1, string.length - 1)

        val injectableResult = doGetResult(null, placeholder, injections, context)

        if (injectableResult != null) {
            return injectableResult
        }

        val parts = placeholder.split("_", limit = 2)

        if (parts.size == 2) {
            val plugin = EcoPlugin.getPlugin(parts[0])

            if (plugin != null) {
                return doGetResult(plugin, parts[1], null, context)
            }
        }

        return null
    }
}
