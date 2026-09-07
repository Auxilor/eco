package com.willfp.eco.core.progression

/**
 * Why a progression loop stopped.
 *
 * [INVALID_REQUIREMENT] is the interesting one: it means the configured curve produced a value
 * that could not be consumed, so the loop stopped defensively. The caller should log it once
 * per element rather than per grant, since it will recur on every XP gain until the config is
 * fixed.
 */
enum class StopReason {
    /** The player cannot afford the next level yet. The normal outcome. */
    NOT_ENOUGH_XP,

    /** The maximum level has been reached. */
    MAX_LEVEL,

    /** The curve returned an unusable requirement - a misconfiguration. */
    INVALID_REQUIREMENT
}
