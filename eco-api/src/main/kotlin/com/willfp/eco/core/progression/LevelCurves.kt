package com.willfp.eco.core.progression

/**
 * Parses the level-curve keys every levelling plugin shares, with validation.
 *
 * Problems are returned rather than thrown or logged. Log them once when the element is
 * loaded - a per-grant log would spam the console on every XP gain, which is why the previous
 * implementations logged nothing at all and let the failure surface as a hang instead.
 */
object LevelCurves {
    /** A parsed curve plus everything wrong with the config that produced it. */
    data class ParsedCurve(
        val curve: LevelCurve,
        val problems: List<CurveProblem>
    )

    /**
     * Parse a curve.
     *
     * @param formula      The `xp-formula` value, or null.
     * @param requirements The `xp-requirements` / `level-xp-requirements` list, or null.
     *                     Pass the raw configured list; do not prepend a zero.
     * @param maxLevel     The configured `max-level`, or null.
     * @param startLevel   The level holders begin at. Required, and different per plugin -
     *                     see the parity contract. An error here reprices every level
     *                     silently, so it is never defaulted.
     * @param freeFirstLevel Whether reaching `startLevel + 1` costs nothing.
     * @param evaluate     Evaluates the formula for a level.
     * @return The curve and any problems.
     */
    @JvmStatic
    fun parse(
        formula: String?,
        requirements: List<Double>?,
        maxLevel: Int?,
        startLevel: Int,
        freeFirstLevel: Boolean = false,
        evaluate: (String, Int) -> Double
    ): ParsedCurve {
        val problems = mutableListOf<CurveProblem>()

        if (maxLevel != null && maxLevel < 1) {
            problems += CurveProblem("max-level", "max-level is $maxLevel, which is below 1")
        }

        if (formula != null && !requirements.isNullOrEmpty()) {
            problems += CurveProblem(
                "xp-requirements",
                "both xp-formula and xp-requirements are set - xp-formula wins"
            )
        }

        if (formula != null) {
            return ParsedCurve(LevelCurve.Formula(formula, startLevel, maxLevel, evaluate), problems)
        }

        if (!requirements.isNullOrEmpty()) {
            val freeOffset = if (freeFirstLevel) 1 else 0

            for ((index, requirement) in requirements.withIndex()) {
                if (!requirement.isFinite() || requirement <= 0.0) {
                    // The level this entry prices, in the caller's own numbering. Hardcoding
                    // `index + 2` is right only for a startLevel of 1 with no free level, and
                    // would misname the level for every other caller - worse than no warning,
                    // since it sends the server owner to the wrong config line.
                    val level = startLevel + index + 1 + freeOffset

                    problems += CurveProblem(
                        "xp-requirements[$index]",
                        "requirement for level $level is $requirement, which is not a " +
                            "usable amount - progression will stop at level ${level - 1}"
                    )
                }
            }

            val naturalMax = startLevel + requirements.size + freeOffset

            val clamped = if (maxLevel != null && maxLevel > naturalMax) {
                problems += CurveProblem(
                    "max-level",
                    "max-level ($maxLevel) is higher than xp-requirements allows " +
                        "($naturalMax) - clamped to $naturalMax"
                )
                naturalMax
            } else {
                maxLevel
            }

            return ParsedCurve(
                LevelCurve.Requirements(requirements, startLevel, clamped, freeFirstLevel),
                problems
            )
        }

        problems += CurveProblem(
            "xp-formula",
            "neither xp-formula nor xp-requirements is set - this element cannot level up"
        )

        return ParsedCurve(LevelCurve.None, problems)
    }
}
