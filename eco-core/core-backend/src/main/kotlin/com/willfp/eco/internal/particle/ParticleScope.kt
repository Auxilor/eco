package com.willfp.eco.internal.particle

import org.bukkit.NamespacedKey

/**
 * Loader-time scope tracking parameter overrides being applied to a preset
 * and the chain of preset names being resolved (for cycle detection).
 */
internal data class ParticleScope(
    val parameters: Map<String, Any> = emptyMap(),
    val resolvingChain: List<NamespacedKey> = emptyList()
) {
    fun withParameters(extra: Map<String, Any>): ParticleScope =
        copy(parameters = parameters + extra)

    fun resolving(key: NamespacedKey): ParticleScope {
        if (key in resolvingChain) {
            throw com.willfp.eco.internal.particle.config.CyclicPresetException(resolvingChain + key)
        }
        return copy(resolvingChain = resolvingChain + key)
    }
}