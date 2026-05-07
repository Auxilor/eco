package com.willfp.eco.core.particle;

/**
 * Where a registered particle came from. Drives reload semantics:
 * {@link #PLUGIN} entries survive reload; {@link #CONFIG} entries are
 * cleared and rebuilt on every {@code Particles.reloadConfigs()}.
 */
public enum ParticleOrigin {
    /** Registered programmatically via {@code Particles.register(...)}. */
    PLUGIN,

    /** Loaded via {@code Particles.loadFromConfig(...)}. */
    CONFIG
}