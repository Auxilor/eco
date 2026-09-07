package com.willfp.eco.core.progression

/**
 * How much XP is needed to reach each level.
 *
 * Levels start at 1, so the first requirement is the cost of reaching level 2 - matching the
 * model EcoMinions already uses. This is deliberately not the `listOf(0) + requirements`
 * padding the older plugins used: a zero-cost first level is what let a stray XP grant unlock
 * a job or adopt a pet the player never acquired.
 *
 * ## The one invariant everything else rests on
 *
 * [xpToReach] never throws and never returns a value that is zero, negative, or non-finite.
 * Anything it cannot serve becomes [Double.POSITIVE_INFINITY], which reads as "unreachable"
 * and therefore terminates any progression loop. Every unbounded-loop and StackOverflowError
 * in the source audits traces back to a requirement of `<= 0` reaching a comparison; this
 * type is where that is made impossible.
 */
sealed interface LevelCurve {
    /** The highest level this curve can reach. */
    val maxLevel: Int

    /**
     * A level that is reached for free by any positive XP grant, or null.
     *
     * Exists solely to preserve EcoJobs' and EcoPets' existing join/adopt flow, where a
     * player sits at level 0 until their first XP. Applied once, outside the progression
     * loop - see [LevelCurve.Requirements.freeLevel].
     */
    val freeLevel: Int?
        get() = null

    /**
     * The XP needed to go from [level] - 1 to [level].
     *
     * @return A strictly positive value, or [Double.POSITIVE_INFINITY] if unreachable.
     *         Never zero, never negative, never NaN - the progression loop relies on this.
     */
    fun xpToReach(level: Int): Double

    /**
     * An explicit cost per level. Running off the end of the list ends progression.
     *
     * ## Start levels differ between plugins, so the offset is explicit
     *
     * The consuming plugins do not agree on where levelling begins, and each one's config
     * files are written against its own convention. Getting this wrong does not fail loudly -
     * it silently shifts every level's cost by one, or stops progression dead at the start
     * level. So [startLevel] is a required constructor argument with no default:
     *
     * | Plugin        | Start level | `requirements[0]` is the cost of reaching |
     * |---------------|-------------|-------------------------------------------|
     * | EcoMinions    | 1           | level 2                                   |
     * | EcoSkills     | 0           | level 1                                   |
     * | EcoJobs       | 0           | level 2 (see [freeFirstLevel])            |
     * | EcoPets       | 0           | level 2 (see [freeFirstLevel])            |
     * | libreforge    | 1           | level 2                                   |
     *
     * @param requirements       The configured cost list, exactly as written in config.
     * @param startLevel         The level a holder begins at.
     * @param configuredMaxLevel An explicit `max-level`, or null to derive it from the list.
     * @param freeFirstLevel     Whether reaching `startLevel + 1` costs nothing. EcoJobs and
     *                           EcoPets both prepend a zero to their requirement list
     *                           (`listOf(0) + config.getInts(...)`), which makes the first
     *                           level free. That is load-bearing, not a bug: `joinJob` adds
     *                           the job to the player's active list without setting a level,
     *                           so a joined player sits at level 0 and relies on the free
     *                           transition to reach level 1. Preserve it; the auto-unlock
     *                           defect it enabled is fixed by an ownership guard at the XP
     *                           entry point instead.
     */
    class Requirements(
        requirements: List<Double>,
        val startLevel: Int,
        configuredMaxLevel: Int? = null,
        private val freeFirstLevel: Boolean = false
    ) : LevelCurve {
        private val requirements = requirements.toList()

        override val maxLevel: Int =
            configuredMaxLevel ?: (startLevel + requirements.size + if (freeFirstLevel) 1 else 0)

        /**
         * The one level that costs nothing, or null.
         *
         * Handled by [LevelProgression.progress] as a single step *before* its loop, never
         * inside it. A zero-cost level inside the loop would either trip the
         * `required <= 0` termination guard (stopping progression) or, if that guard were
         * relaxed, become the exact infinite loop this whole plan exists to remove. Modelling
         * it as a one-shot step keeps both properties: the behaviour is preserved and the
         * loop stays provably bounded.
         */
        override val freeLevel: Int? = if (freeFirstLevel) startLevel + 1 else null

        override fun xpToReach(level: Int): Double {
            if (level <= startLevel) {
                return Double.POSITIVE_INFINITY
            }

            val index = level - startLevel - 1 - if (freeFirstLevel) 1 else 0

            if (index < 0) {
                // The free level, priced through xpToReach rather than the freeLevel path.
                // Infinity, not zero: callers that price a level directly (progress bars,
                // "xp required" placeholders) must never divide by it.
                return Double.POSITIVE_INFINITY
            }

            // getOrNull, not indexing: a max-level configured larger than the list used to
            // throw IndexOutOfBoundsException mid-XP-gain rather than at load time.
            val required = this.requirements.getOrNull(index) ?: return Double.POSITIVE_INFINITY
            return if (required.isFinite() && required > 0.0) required else Double.POSITIVE_INFINITY
        }
    }

    /**
     * An expression in `%level%`, evaluated for the level being reached.
     *
     * @param expression         The raw expression, kept for error messages.
     * @param startLevel         The level holders begin at. Required and never defaulted, for
     *                           the same reason as [Requirements.startLevel].
     * @param configuredMaxLevel An explicit `max-level`, or null for unbounded.
     * @param evaluate           Evaluates the expression for a level. Injected so that curve
     *                           arithmetic is testable without an expression engine, and so
     *                           that eco's evaluator stays out of the pure core.
     */
    class Formula(
        val expression: String,
        val startLevel: Int,
        configuredMaxLevel: Int? = null,
        private val evaluate: (String, Int) -> Double
    ) : LevelCurve {
        override val maxLevel: Int = configuredMaxLevel ?: Int.MAX_VALUE

        override fun xpToReach(level: Int): Double {
            // Bounded by startLevel, not a hardcoded 2. EcoBattlepass's tiers start at 0, so a
            // `level < 2` guard would price the 0 -> 1 tier at infinity and freeze every pass
            // at tier 0. The start level is a required argument for the same reason it is on
            // Requirements: there is no safe default, and getting it wrong fails silently.
            if (level <= startLevel || level > maxLevel) {
                return Double.POSITIVE_INFINITY
            }

            val required = try {
                evaluate(expression, level)
            } catch (e: Exception) {
                // A config expression must not be able to throw into a tick. Treat a broken
                // evaluation as unreachable; the parse-time problem list is where the server
                // owner is told about it.
                return Double.POSITIVE_INFINITY
            }

            return if (required.isFinite() && required > 0.0) required else Double.POSITIVE_INFINITY
        }
    }

    /**
     * A curve that never advances, used when levelling is disabled or misconfigured.
     *
     * Replaces the previous behaviour of throwing IllegalStateException from the XP-required
     * lookup: a broken element should stop progressing, not break the tick it is read on.
     */
    object None : LevelCurve {
        override val maxLevel: Int = 1

        override fun xpToReach(level: Int): Double = Double.POSITIVE_INFINITY
    }
}
