package com.willfp.eco.core.progression;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LevelCurveTest {
    @Test
    void requirementsCurveCostsTheListedAmount() {
        // requirements[0] is the cost of reaching level 2, since players start at level 1.
        LevelCurve curve = new LevelCurve.Requirements(List.of(10.0, 20.0, 30.0), 1, null, false);

        assertEquals(10.0, curve.xpToReach(2));
        assertEquals(20.0, curve.xpToReach(3));
        assertEquals(30.0, curve.xpToReach(4));
    }

    @Test
    void levelsAtOrBelowTheStartLevelAreUnreachable() {
        // Without freeFirstLevel there is no zero-cost level; the start level itself and
        // anything below it can never be "reached", so pricing them yields infinity rather
        // than an index error or a free promotion.
        LevelCurve curve = new LevelCurve.Requirements(List.of(10.0), 1, null, false);

        assertEquals(Double.POSITIVE_INFINITY, curve.xpToReach(1));
        assertEquals(Double.POSITIVE_INFINITY, curve.xpToReach(0));
        assertEquals(Double.POSITIVE_INFINITY, curve.xpToReach(-5));
    }

    @Test
    void startLevelShiftsTheWholeCurve() {
        // EcoSkills starts at 0, EcoMinions at 1, and the same config list must price
        // differently for each. Getting this wrong reprices every level on every server.
        LevelCurve fromZero = new LevelCurve.Requirements(List.of(10.0, 20.0), 0, null, false);
        LevelCurve fromOne = new LevelCurve.Requirements(List.of(10.0, 20.0), 1, null, false);

        assertEquals(10.0, fromZero.xpToReach(1));
        assertEquals(20.0, fromZero.xpToReach(2));

        assertEquals(Double.POSITIVE_INFINITY, fromOne.xpToReach(1));
        assertEquals(10.0, fromOne.xpToReach(2));
    }

    @Test
    void freeFirstLevelIsExposedAndNotPricedAsZero() {
        // EcoJobs/EcoPets: joinJob leaves the player at level 0 and the 0 -> 1 transition is
        // free. It is exposed as freeLevel and handled once by the progression loop; pricing
        // it through xpToReach must still return infinity so progress bars never divide by it.
        LevelCurve curve = new LevelCurve.Requirements(List.of(100.0, 200.0), 0, null, true);

        assertEquals(1, curve.getFreeLevel());
        assertEquals(Double.POSITIVE_INFINITY, curve.xpToReach(1));
        assertEquals(100.0, curve.xpToReach(2));
        assertEquals(200.0, curve.xpToReach(3));
    }

    @Test
    void requirementsCurveReturnsInfinityPastEnd() {
        // Regression: indexing past the list used to throw IndexOutOfBoundsException mid-gain.
        LevelCurve curve = new LevelCurve.Requirements(List.of(10.0, 20.0), 1, null, false);

        assertEquals(Double.POSITIVE_INFINITY, curve.xpToReach(4));
        assertEquals(Double.POSITIVE_INFINITY, curve.xpToReach(1_000_000));
    }

    @Test
    void requirementsCurveDerivesMaxLevelFromTheList() {
        assertEquals(4, new LevelCurve.Requirements(List.of(10.0, 20.0, 30.0), 1, null, false).getMaxLevel());
        assertEquals(2, new LevelCurve.Requirements(List.of(10.0, 20.0, 30.0), 1, 2, false).getMaxLevel());
    }

    @Test
    void formulaCurveEvaluatesPerLevel() {
        LevelCurve curve = new LevelCurve.Formula("x", 1, null, (expression, level) -> level * 100.0);

        assertEquals(200.0, curve.xpToReach(2));
        assertEquals(500.0, curve.xpToReach(5));
    }

    @Test
    void formulaCurveClampsNonPositiveToInfinity() {
        // The whole recursion-bomb family starts here: a formula that yields <= 0 makes the
        // level-up condition permanently true. Infinity makes it permanently false instead.
        assertEquals(Double.POSITIVE_INFINITY,
                new LevelCurve.Formula("x", 1, null, (e, l) -> 0.0).xpToReach(2));
        assertEquals(Double.POSITIVE_INFINITY,
                new LevelCurve.Formula("x", 1, null, (e, l) -> -50.0).xpToReach(2));
    }

    @Test
    void formulaCurveClampsNonFiniteToInfinity() {
        assertEquals(Double.POSITIVE_INFINITY,
                new LevelCurve.Formula("x", 1, null, (e, l) -> Double.NaN).xpToReach(2));
        assertEquals(Double.POSITIVE_INFINITY,
                new LevelCurve.Formula("x", 1, null, (e, l) -> Double.NEGATIVE_INFINITY).xpToReach(2));
    }

    @Test
    void formulaCurveNeverPropagatesAnEvaluatorThrow() {
        // Expression evaluation runs on user config; a throw here used to reach the tick.
        LevelCurve curve = new LevelCurve.Formula("x", 1, null, (e, l) -> {
            throw new IllegalStateException("bad expression");
        });

        assertEquals(Double.POSITIVE_INFINITY, curve.xpToReach(2));
    }

    @Test
    void formulaCurveRespectsItsStartLevel() {
        // EcoBattlepass's tiers start at 0, so the 0 -> 1 tier must be priced, not treated as
        // unreachable. A hardcoded `level < 2` guard here would freeze every pass at tier 0.
        LevelCurve fromZero = new LevelCurve.Formula("x", 0, null, (e, l) -> l * 100.0);

        assertEquals(Double.POSITIVE_INFINITY, fromZero.xpToReach(0));
        assertEquals(100.0, fromZero.xpToReach(1));
        assertEquals(200.0, fromZero.xpToReach(2));
    }

    @Test
    void noneCurveNeverAdvances() {
        assertEquals(Double.POSITIVE_INFINITY, LevelCurve.None.INSTANCE.xpToReach(2));
        assertEquals(1, LevelCurve.None.INSTANCE.getMaxLevel());
    }
}
