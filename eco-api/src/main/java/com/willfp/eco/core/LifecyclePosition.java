package com.willfp.eco.core;

/**
 * Marks a position in a lifecycle (e.g. enable, reload, etc).
 * <p>
 * Used by the task registration methods on {@link EcoPlugin}, such as
 * {@link EcoPlugin#onEnable(LifecyclePosition, Runnable)}, to control whether a task runs
 * before or after the plugin's own handler for that lifecycle stage.
 */
public enum LifecyclePosition {
    /**
     * Run at the start of the lifecycle, before the plugin's own handler.
     */
    START,

    /**
     * Run at the end of the lifecycle, after the plugin's own handler.
     */
    END
}
