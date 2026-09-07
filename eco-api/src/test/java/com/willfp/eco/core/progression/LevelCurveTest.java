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

    @Test
    void parseClampsMaxLevelToRequirementsSize() {
        // Regression: max-level larger than the list used to reach an unchecked index.
        LevelCurves.ParsedCurve parsed = LevelCurves.parse(
                null, List.of(10.0, 20.0), 500, 1, false, (e, l) -> 0.0
        );

        assertEquals(3, parsed.getCurve().getMaxLevel());
        assertEquals(1, parsed.getProblems().size());
        assertTrue(parsed.getProblems().get(0).getMessage().contains("max-level"));
    }

    @Test
    void parsePrefersFormulaAndSaysSo() {
        LevelCurves.ParsedCurve parsed = LevelCurves.parse(
                "100", List.of(10.0), null, 1, false, (e, l) -> 100.0
        );

        assertInstanceOf(LevelCurve.Formula.class, parsed.getCurve());
        assertEquals(1, parsed.getProblems().size());
    }

    @Test
    void parseWithNeitherKeyYieldsNoneAndAProblem() {
        // Previously an IllegalStateException thrown from a hot path.
        LevelCurves.ParsedCurve parsed = LevelCurves.parse(null, null, null, 1, false, (e, l) -> 0.0);

        assertSame(LevelCurve.None.INSTANCE, parsed.getCurve());
        assertEquals(1, parsed.getProblems().size());
    }

    @Test
    void parseRejectsNonPositiveRequirementEntries() {
        LevelCurves.ParsedCurve parsed = LevelCurves.parse(
                null, List.of(10.0, 0.0, 30.0), null, 1, false, (e, l) -> 0.0
        );

        assertFalse(parsed.getProblems().isEmpty());
        // The bad entry is unreachable rather than free.
        assertEquals(Double.POSITIVE_INFINITY, parsed.getCurve().xpToReach(3));
    }

    @Test
    void parseNamesTheOffendingLevelInTheCallersOwnNumbering() {
        // Hardcoding `index + 2` here is right only for a startLevel of 1 with no free level,
        // and would misname the level in every EcoSkills, EcoJobs and EcoPets warning - which
        // is worse than no warning, because it sends the owner to the wrong config line.
        LevelCurves.ParsedCurve skills = LevelCurves.parse(
                null, List.of(10.0, 0.0), null, 0, false, (e, l) -> 0.0
        );
        assertTrue(skills.getProblems().get(0).getMessage().contains("level 2"));

        LevelCurves.ParsedCurve jobs = LevelCurves.parse(
                null, List.of(10.0, 0.0), null, 0, true, (e, l) -> 0.0
        );
        assertTrue(jobs.getProblems().get(0).getMessage().contains("level 3"));
    }
}
