package com.willfp.eco.internal.spigot.math

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import java.util.Objects
import java.util.concurrent.TimeUnit

private val evaluationCache: Cache<Int, Double> = Caffeine.newBuilder()
    .expireAfterWrite(100, TimeUnit.MILLISECONDS)
    .build()

class DelegatedCrunchHandler(
    private val handler: CrunchHandler
): CrunchHandler {
    override fun evaluate(expression: String, context: PlaceholderContext): Double {
        val hash = Objects.hash(
            expression,
            context.player?.uniqueId,
            context.injectableContext
        )

        return evaluationCache.get(hash) {
            handler.evaluate(expression, context)
                .let { if (!it.isFinite()) 0.0 else it } // Fixes NaN bug.
        }
    }
}
