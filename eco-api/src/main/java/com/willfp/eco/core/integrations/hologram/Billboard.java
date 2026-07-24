package com.willfp.eco.core.integrations.hologram;

/**
 * How a hologram rotates relative to the viewer.
 */
public enum Billboard {
    /** Never rotates. */
    FIXED,
    /** Rotates around the vertical axis. */
    VERTICAL,
    /** Rotates around the horizontal axis. */
    HORIZONTAL,
    /** Always faces the viewer. */
    CENTER
}
