package com.willfp.eco.core.integrations.model;

/**
 * A state that a model plays an animation in by itself.
 */
public enum ModelState {
    /**
     * Standing still.
     */
    IDLE,

    /**
     * Walking.
     */
    WALK,

    /**
     * Walking sideways.
     */
    STRAFE,

    /**
     * Starting a jump.
     */
    JUMP_START,

    /**
     * In the air during a jump.
     */
    JUMP,

    /**
     * Landing from a jump.
     */
    JUMP_END,

    /**
     * Hovering in the air.
     */
    HOVER,

    /**
     * Flying.
     */
    FLY,

    /**
     * Spawning.
     */
    SPAWN,

    /**
     * Dying.
     */
    DEATH
}
