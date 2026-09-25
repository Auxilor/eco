package com.willfp.eco.core.progression

/**
 * The outcome of applying an XP grant, as a value.
 *
 * Nothing here is persisted or dispatched; the caller decides what to commit. That separation
 * is deliberate - it is what lets each plugin keep its own cancellable-event semantics while
 * sharing the arithmetic, and it is what stops a cancelled level-up from leaving half-written
 * state behind.
 */
data class LevelChange(
    /** The level after the grant. */
    val newLevel: Int,

    /** The XP banked toward the next level after the grant. Always finite and >= 0. */
    val newXp: Double,

    /** Every level crossed, or null if none were. Fire one event per level in this range. */
    val levelsGained: IntRange?,

    /** Why the loop stopped. */
    val stopReason: StopReason
) {
    /** Whether any level was gained. */
    val leveledUp: Boolean
        get() = levelsGained != null
}
