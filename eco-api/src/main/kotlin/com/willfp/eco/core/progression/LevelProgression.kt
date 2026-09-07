package com.willfp.eco.core.progression

/**
 * Pure XP-to-level arithmetic.
 *
 * ## Termination
 *
 * The loop below stops on any of three independent conditions, so it terminates even if the
 * other two are useless:
 *
 * 1. `level >= curve.maxLevel` - the product cap.
 * 2. `required` is not finite or not positive - the misconfiguration guard.
 * 3. `xp < required` - the ordinary case.
 *
 * Condition 2 is what makes this safe. Every StackOverflowError and server hang in the audits
 * this replaced came from a `<= 0` requirement making condition 3 permanently false while
 * condition 1 was effectively absent (EcoSkills defaults `max-level` to `Int.MAX_VALUE`, and
 * EcoBattlepass checked no cap at all in the XP path). Do not remove condition 2 on the
 * grounds that the curve "should" be validated at load: a formula is evaluated per level, so
 * a curve that is fine at level 1 can be broken at level 400.
 */
object LevelProgression {
    /**
     * Apply an XP grant.
     *
     * @param curve        The level curve.
     * @param currentLevel The player's current level.
     * @param currentXp    XP already banked toward the next level.
     * @param amount       XP to add. Non-positive and non-finite amounts are no-ops; reject
     *                     them at your entry point if you want to tell the caller why.
     * @param clearXpAtMaxLevel Whether to discard leftover XP on reaching max level. Pass
     *                     `false` to keep banking it, which is what EcoSkills, EcoJobs and
     *                     EcoPets do today; `true` matches EcoMinions. Defaulted to `false`
     *                     so that a caller who does not think about it keeps the majority
     *                     behaviour rather than silently discarding player progress. It is
     *                     applied uniformly, whether max level was crossed into by this grant
     *                     or already held before it - there is one policy, and this is it.
     * @return The change to apply.
     */
    @JvmStatic
    @JvmOverloads
    fun progress(
        curve: LevelCurve,
        currentLevel: Int,
        currentXp: Double,
        amount: Double,
        clearXpAtMaxLevel: Boolean = false
    ): LevelChange {
        val startingXp = if (currentXp.isFinite() && currentXp > 0.0) currentXp else 0.0

        if (!amount.isFinite() || amount <= 0.0) {
            return LevelChange(currentLevel, startingXp, null, StopReason.NOT_ENOUGH_XP)
        }

        if (currentLevel >= curve.maxLevel) {
            val banked = if (clearXpAtMaxLevel) 0.0 else startingXp + amount
            return LevelChange(currentLevel, banked, null, StopReason.MAX_LEVEL)
        }

        var level = currentLevel
        var xp = startingXp + amount
        val firstGained = currentLevel + 1
        var reason = StopReason.NOT_ENOUGH_XP

        // The free level, applied exactly once and outside the loop below. Inside the loop a
        // zero cost would either trip the termination guard or, if that guard were relaxed
        // for it, reintroduce the unbounded loop. Once, here, is safe.
        if (curve.freeLevel == level + 1 && level < curve.maxLevel) {
            level++
        }

        while (level < curve.maxLevel) {
            val required = curve.xpToReach(level + 1)

            if (!required.isFinite() || required <= 0.0) {
                reason = StopReason.INVALID_REQUIREMENT
                break
            }

            if (xp < required) {
                break
            }

            xp -= required
            level++
        }

        if (level >= curve.maxLevel) {
            reason = StopReason.MAX_LEVEL

            // Whether leftover XP is banked or discarded at max level is the caller's policy,
            // expressed once through clearXpAtMaxLevel. The plugins disagree today and this
            // does not change any of them: EcoSkills, EcoJobs and EcoPets all keep
            // accumulating past max level (their level-up branch simply stops being taken,
            // and the else-branch writes the running total), so they pass false, which is the
            // default; EcoMinions zeroes it and passes true.
            if (clearXpAtMaxLevel) {
                xp = 0.0
            }
        }

        if (level == currentLevel) {
            // No level gained: bank the total, including on a broken curve. Discarding the
            // grant instead would destroy the player's XP for the duration of a server
            // owner's config mistake, and silently - they would see XP go in and nothing come
            // out. Banking it means the progress is still there once the curve is fixed, and
            // it is the same policy the max-level branch above applies for the same reason.
            return LevelChange(currentLevel, xp, null, reason)
        }

        return LevelChange(level, xp, firstGained..level, reason)
    }

    /**
     * A progress fraction safe to render.
     *
     * Returns a value in `0.0..1.0`, never `NaN` and never infinite. A zero or unusable
     * requirement reads as complete rather than as a division by zero, and max level always
     * reads as complete rather than as 0% forever.
     *
     * @param currentXp   XP banked toward the next level.
     * @param required    XP required for the next level.
     * @param atMaxLevel  Whether the holder is at the maximum level.
     * @return The fraction, in 0.0..1.0.
     */
    @JvmStatic
    fun progressFraction(currentXp: Double, required: Double, atMaxLevel: Boolean): Double {
        if (atMaxLevel) {
            return 1.0
        }

        if (!required.isFinite() || required <= 0.0) {
            return 1.0
        }

        if (!currentXp.isFinite()) {
            return 0.0
        }

        return (currentXp / required).coerceIn(0.0, 1.0)
    }

    /**
     * The level reached by a cumulative total, for threshold ladders rather than XP banks.
     *
     * Used where progression is "you have mined 5,000 blocks, that is tier 4" rather than
     * "you have 40 XP toward the next level" - EcoCollections' tier system. Thresholds must be
     * strictly increasing; a non-increasing entry ends the ladder there rather than producing
     * a tier that can be skipped past.
     *
     * @param thresholds The cumulative total required for tier 1, 2, 3...
     * @param total      The cumulative total held.
     * @return The tier reached, 0 if below the first threshold.
     */
    @JvmStatic
    fun levelForCumulative(thresholds: List<Double>, total: Double): Int {
        if (!total.isFinite() || total <= 0.0) {
            return 0
        }

        var reached = 0
        var previous = Double.NEGATIVE_INFINITY

        for (threshold in thresholds) {
            if (!threshold.isFinite() || threshold <= previous) {
                break
            }

            if (total < threshold) {
                break
            }

            previous = threshold
            reached++
        }

        return reached
    }
}
