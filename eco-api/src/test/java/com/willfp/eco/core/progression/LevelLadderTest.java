package com.willfp.eco.core.progression;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LevelLadderTest {
    private static final List<Double> TIERS = List.of(10.0, 50.0, 200.0);

    @Test
    void belowFirstThresholdIsTierZero() {
        assertEquals(0, LevelProgression.levelForCumulative(TIERS, 0.0));
        assertEquals(0, LevelProgression.levelForCumulative(TIERS, 9.9));
    }

    @Test
    void thresholdsAreInclusive() {
        assertEquals(1, LevelProgression.levelForCumulative(TIERS, 10.0));
        assertEquals(3, LevelProgression.levelForCumulative(TIERS, 200.0));
    }

    @Test
    void beyondTheLastThresholdCapsAtTheLastTier() {
        assertEquals(3, LevelProgression.levelForCumulative(TIERS, 1_000_000.0));
    }

    @Test
    void nonIncreasingThresholdsEndTheLadder() {
        // A config with a duplicated or decreasing tier requirement must not create a tier
        // that can be skipped past, which is what makes tier-up rewards unreachable.
        assertEquals(2, LevelProgression.levelForCumulative(List.of(10.0, 50.0, 50.0, 90.0), 100.0));
        assertEquals(2, LevelProgression.levelForCumulative(List.of(10.0, 50.0, 20.0, 90.0), 100.0));
    }

    @Test
    void nonFiniteInputsAreSafe() {
        assertEquals(0, LevelProgression.levelForCumulative(TIERS, Double.NaN));
        assertEquals(0, LevelProgression.levelForCumulative(TIERS, Double.NEGATIVE_INFINITY));
        assertEquals(1, LevelProgression.levelForCumulative(List.of(10.0, Double.NaN, 20.0), 100.0));
    }

    @Test
    void emptyLadderIsAlwaysTierZero() {
        assertEquals(0, LevelProgression.levelForCumulative(List.of(), 1_000.0));
    }
}
