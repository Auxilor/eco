package com.willfp.eco.core.progression;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class LevelProgressionTest {
    private LevelCurve flat(double cost) {
        return new LevelCurve.Formula("x", 1, Integer.MAX_VALUE, (e, l) -> cost);
    }

    @Test
    void bankedXpBelowTheRequirementDoesNotLevel() {
        LevelChange change = LevelProgression.progress(flat(100.0), 1, 0.0, 40.0);

        assertEquals(1, change.getNewLevel());
        assertEquals(40.0, change.getNewXp());
        assertNull(change.getLevelsGained());
        assertEquals(StopReason.NOT_ENOUGH_XP, change.getStopReason());
    }

    @Test
    void crossingOneLevelCarriesTheRemainder() {
        LevelChange change = LevelProgression.progress(flat(100.0), 1, 0.0, 130.0);

        assertEquals(2, change.getNewLevel());
        assertEquals(30.0, change.getNewXp());
        assertEquals(2, change.getLevelsGained().getFirst());
        assertEquals(2, change.getLevelsGained().getLast());
    }

    @Test
    void progressReportsEveryCrossedLevel() {
        // Each crossed level must be reported so the caller can fire one event and one reward
        // per level; a multi-level grant must not swallow the levels in between.
        LevelChange change = LevelProgression.progress(flat(100.0), 1, 0.0, 350.0);

        assertEquals(4, change.getNewLevel());
        assertEquals(50.0, change.getNewXp());
        assertEquals(2, change.getLevelsGained().getFirst());
        assertEquals(4, change.getLevelsGained().getLast());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void progressTerminatesOnZeroRequirement() {
        // The recursion bomb. Previously: infinite recursion -> StackOverflowError.
        LevelChange change = LevelProgression.progress(flat(0.0), 1, 0.0, 1.0);

        assertEquals(1, change.getNewLevel());
        assertEquals(1.0, change.getNewXp());
        assertNull(change.getLevelsGained());
        assertEquals(StopReason.INVALID_REQUIREMENT, change.getStopReason());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void progressTerminatesOnNegativeRequirement() {
        LevelChange change = LevelProgression.progress(flat(-10.0), 1, 0.0, 1.0);

        assertEquals(1, change.getNewLevel());
        assertEquals(StopReason.INVALID_REQUIREMENT, change.getStopReason());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void nanRequirementStopsWithInvalidCurve() {
        // Distinct from the <= 0 case: `xp >= NaN` is always false, which previously meant
        // level-ups silently never happened rather than happening forever.
        LevelChange change = LevelProgression.progress(flat(Double.NaN), 1, 0.0, 1_000_000.0);

        assertEquals(1, change.getNewLevel());
        assertEquals(StopReason.INVALID_REQUIREMENT, change.getStopReason());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void progressTerminatesWithIntMaxLevelAndBadCurve() {
        // EcoSkills' max-level default is Int.MAX_VALUE, so termination must not rely on it.
        LevelChange change = LevelProgression.progress(flat(0.0), 1, 0.0, Double.MAX_VALUE);

        assertEquals(1, change.getNewLevel());
        assertEquals(StopReason.INVALID_REQUIREMENT, change.getStopReason());
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void progressCrossesManyLevelsWithoutStackGrowth() {
        // 100k levels in one grant: a loop handles this, recursion would overflow.
        LevelChange change = LevelProgression.progress(flat(1.0), 1, 0.0, 100_000.0);

        assertEquals(100_001, change.getNewLevel());
        assertEquals(0.0, change.getNewXp());
    }

    @Test
    void progressStopsAtMaxLevelAndBanksTheRemainder() {
        // Leftover XP is BANKED at max level by default, not cleared. EcoSkills, EcoJobs and
        // EcoPets all keep accumulating past max today, and `clearXpAtMaxLevel` defaults to
        // false so a caller who does not think about it keeps that behaviour; EcoMinions
        // passes true. There is exactly one policy and it is the flag - an earlier draft of
        // this plan cleared on "crossed into max" but banked on "already at max", which no
        // implementation can satisfy alongside freeFirstLevelIsAppliedOnlyOnce below.
        LevelCurve curve = new LevelCurve.Requirements(List.of(100.0, 100.0), 1, null, false);
        LevelChange change = LevelProgression.progress(curve, 1, 0.0, 100_000.0);

        assertEquals(3, change.getNewLevel());
        assertEquals(99_800.0, change.getNewXp(), "leftover xp is banked by default");
        assertEquals(StopReason.MAX_LEVEL, change.getStopReason());

        LevelChange cleared = LevelProgression.progress(curve, 1, 0.0, 100_000.0, true);
        assertEquals(0.0, cleared.getNewXp(), "clearXpAtMaxLevel discards it instead");
    }

    @Test
    void alreadyAtMaxLevelIsANoOp() {
        LevelCurve curve = new LevelCurve.Requirements(List.of(100.0), 1, null, false);
        LevelChange change = LevelProgression.progress(curve, 2, 0.0, 500.0);

        assertEquals(2, change.getNewLevel());
        assertNull(change.getLevelsGained());
        assertEquals(StopReason.MAX_LEVEL, change.getStopReason());
    }

    @Test
    void zeroAndNegativeGrantsAreNoOps() {
        assertEquals(1, LevelProgression.progress(flat(100.0), 1, 10.0, 0.0).getNewLevel());
        assertEquals(10.0, LevelProgression.progress(flat(100.0), 1, 10.0, 0.0).getNewXp());
        assertEquals(10.0, LevelProgression.progress(flat(100.0), 1, 10.0, -50.0).getNewXp(),
                "a negative grant must not reduce banked xp through this path");
    }

    @Test
    void freeFirstLevelIsReachedByAnyGrant() {
        LevelCurve curve = new LevelCurve.Requirements(List.of(100.0, 200.0), 0, null, true);
        LevelChange change = LevelProgression.progress(curve, 0, 0.0, 0.001);

        assertEquals(1, change.getNewLevel(), "any positive grant reaches the free level");
        assertEquals(1, change.getLevelsGained().getFirst());
        assertEquals(1, change.getLevelsGained().getLast());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void freeFirstLevelIsAppliedOnlyOnce() {
        // The free level must not be re-consumable, or it is an infinite loop by another name.
        LevelCurve curve = new LevelCurve.Requirements(List.of(100.0), 0, null, true);
        LevelChange change = LevelProgression.progress(curve, 0, 0.0, 150.0);

        assertEquals(2, change.getNewLevel(), "free level, then one paid level");
        assertEquals(50.0, change.getNewXp());
    }

    @Test
    void maxLevelXpIsBankedByDefault() {
        // The same policy as progressStopsAtMaxLevelAndBanksTheRemainder, entered from the
        // other side: already at max on entry rather than crossing into it. Both must agree.
        LevelCurve curve = new LevelCurve.Requirements(List.of(100.0), 0, null, false);
        LevelChange banked = LevelProgression.progress(curve, 1, 0.0, 500.0);

        assertEquals(500.0, banked.getNewXp());
        assertEquals(0.0, LevelProgression.progress(curve, 1, 0.0, 500.0, true).getNewXp());
    }

    @Test
    void progressFractionIsAlwaysFinite() {
        // Regression: %percentage_progress% used to render "Infinity%" and "NaN%".
        assertEquals(1.0, LevelProgression.progressFraction(0.0, 0.0, false));
        assertEquals(1.0, LevelProgression.progressFraction(5.0, Double.POSITIVE_INFINITY, true));
        assertEquals(1.0, LevelProgression.progressFraction(0.0, Double.NaN, false));
        assertEquals(0.5, LevelProgression.progressFraction(50.0, 100.0, false));
        assertEquals(1.0, LevelProgression.progressFraction(500.0, 100.0, false),
                "clamped, never above 1");
        assertEquals(0.0, LevelProgression.progressFraction(-5.0, 100.0, false),
                "clamped, never below 0");
    }

    @Test
    void progressFractionIsOneAtMaxLevel() {
        assertEquals(1.0, LevelProgression.progressFraction(0.0, 100.0, true));
    }
}
