package com.willfp.eco.internal.particle

/**
 * Closed set of YAML `type:` primitive values. The loader dispatches on this enum.
 * Extension is via YAML presets or `Particles.register(NamespacedKey, SpawnableParticle)`,
 * NOT by adding values here.
 */
internal enum class PrimitiveParticleType(val key: String) {
    SIMPLE("simple"),
    TICK("tick"),
    ITERATE("iterate"),
    COMPOSITE("composite");

    companion object {
        fun fromKey(key: String): PrimitiveParticleType? =
            entries.firstOrNull { it.key == key.lowercase() }
    }
}