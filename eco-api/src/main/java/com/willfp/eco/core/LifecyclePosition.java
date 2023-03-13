package com.willfp.eco.core;

/**
 * Marks a position in a lifecycle (e.g. enable, reload, etc).
 */
public enum LifecyclePosition {
    /**
     * Run at the start of the lifecycle.
     */
    START,

    /**
     * Run at the end of the lifecycle.
     */
    END
}
