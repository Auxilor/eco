package com.willfp.eco.internal.spigot.math

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import java.util.concurrent.TimeUnit

class DelegatedExpressionHandler(
    plugin: EcoPlugin,
    private val handler: ExpressionHandler
) : ExpressionHandler {
    private val evaluationCache: Cache<Int, Double?> = Caffeine.newBuilder()
        .expireAfterWrite(plugin.configYml.getInt("math-cache-ttl").toLong(), TimeUnit.MILLISECONDS)
        .build()

    override fun evaluate(expression: String, context: PlaceholderContext): Double? {
        // Peak performance (totally not having fun with bitwise operators)
        val hash = (((expression.hashCode() shl 5) - expression.hashCode()) xor
                (context.player?.uniqueId?.hashCode() ?: 0)
                ) xor context.injectableContext.hashCode()

        return evaluationCache.get(hash) {
            handler.evaluate(expression, context)
                .let { if (it?.isFinite() != true) null else it } // Fixes NaN bug.
        }
    }
}
