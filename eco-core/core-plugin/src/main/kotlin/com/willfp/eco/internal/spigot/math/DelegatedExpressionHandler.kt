package com.willfp.eco.internal.spigot.math

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import java.util.UUID
import java.util.concurrent.TimeUnit

data class ExpressionCacheKey(val expression: String, val playerUUID: UUID?, val injectableContext: Any?)

class DelegatedExpressionHandler(
    plugin: EcoPlugin,
    private val handler: ExpressionHandler
) : ExpressionHandler {
    private val evaluationCache: Cache<ExpressionCacheKey, Double?> = Caffeine.newBuilder()
        .expireAfterWrite(plugin.configYml.getInt("math-cache-ttl").toLong(), TimeUnit.MILLISECONDS)
        .buildAsync<ExpressionCacheKey, Double?>()
        .synchronous()

    override fun evaluate(expression: String, context: PlaceholderContext): Double? {
        expression.fastToDoubleOrNull()?.let { return it }

        val cacheKey = ExpressionCacheKey(
            expression,
            context.player?.uniqueId,
            context.injectableContext
        )

        return evaluationCache.get(cacheKey) {
            handler.evaluate(expression, context)
                .let { if (it?.isFinite() != true) null else it } // Fixes NaN bug.
        }
    }
}
