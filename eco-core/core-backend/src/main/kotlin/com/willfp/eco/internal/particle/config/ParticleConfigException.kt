package com.willfp.eco.internal.particle.config

/**
 * Thrown by the loader when a YAML entry is malformed. Carries the entry name
 * and a field path for diagnostics.
 */
class ParticleConfigException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)