package com.willfp.eco.core.progression

/**
 * Something wrong with a configured level curve, found at parse time.
 *
 * Returned rather than logged so the core stays pure; the caller logs it once, with its own
 * plugin logger and its own element id for context.
 */
data class CurveProblem(
    /** The config path the problem is at, relative to the levelling section. */
    val path: String,

    /** Human-readable description, phrased for a server owner reading a startup log. */
    val message: String
)
