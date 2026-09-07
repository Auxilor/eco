package com.willfp.eco.core.progression;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProgressionPlaceholdersTest {
    @Test
    void injectsTheFixedNames() {
        assertEquals("5", ProgressionPlaceholders.inject("%level%", "level", 5));
        assertEquals("V", ProgressionPlaceholders.inject("%level_numeral%", "level", 5));
        assertEquals("4", ProgressionPlaceholders.inject("%previous_level%", "level", 5));
        assertEquals("IV", ProgressionPlaceholders.inject("%previous_level_numeral%", "level", 5));
    }

    @Test
    void injectsOffsets() {
        assertEquals("7", ProgressionPlaceholders.inject("%level_2%", "level", 5));
        assertEquals("3", ProgressionPlaceholders.inject("%level_-2%", "level", 5));
        assertEquals("VII", ProgressionPlaceholders.inject("%level_2_numeral%", "level", 5));
    }

    @Test
    void anExplicitPlusIsAccepted() {
        // EcoPets' spawn-egg lore allowed `%level_+2%` while every other copy of this regex
        // only allowed `%level_2%` and `%level_-2%`. The shared pattern is the superset, so
        // migrating the egg path onto it cannot lose a form that already worked.
        assertEquals("7", ProgressionPlaceholders.inject("%level_+2%", "level", 5));
        assertEquals("VII", ProgressionPlaceholders.inject("%level_+2_numeral%", "level", 5));
        assertEquals("7", ProgressionPlaceholders.resolveOffset("level_+2", "level", 5));
    }

    @Test
    void offsetDoesNotSwallowTheNumeralName() {
        // `%level_numeral%` must not be read as an offset of "numeral". The offset pattern
        // requires digits, which is what keeps these two apart.
        assertNull(ProgressionPlaceholders.resolveOffset("level_numeral", "level", 5));
        assertEquals("V", ProgressionPlaceholders.inject("%level_numeral%", "level", 5));
    }

    @Test
    void typeIsRespectedSoATierLadderNeverSeesLevel() {
        assertEquals("3", ProgressionPlaceholders.inject("%tier%", "tier", 3));
        assertEquals("5", ProgressionPlaceholders.inject("%tier_2%", "tier", 3));

        // A tier config must not resolve level placeholders, and vice versa.
        assertEquals("%level%", ProgressionPlaceholders.inject("%level%", "tier", 3));
        assertNull(ProgressionPlaceholders.resolveOffset("tier_2", "level", 3));
    }

    @Test
    void nonPositiveNumeralsFallBackToDigits() {
        // Roman numerals have no zero and no negatives; an offset can walk below both, and a
        // conversion that throws would do it from inside a tick.
        assertEquals("0", ProgressionPlaceholders.inject("%previous_level_numeral%", "level", 1));
        assertEquals("-2", ProgressionPlaceholders.inject("%level_-3_numeral%", "level", 1));
    }

    @Test
    void replacesEveryOccurrenceAndLeavesOtherTextAlone() {
        assertEquals(
                "You reached 5 (V), up from 4. Next is 6.",
                ProgressionPlaceholders.inject(
                        "You reached %level% (%level_numeral%), up from %previous_level%. Next is %level_1%.",
                        "level",
                        5
                )
        );
    }

    @Test
    void unrelatedPlaceholdersAreUntouched() {
        assertEquals(
                "%player% is 5 and %some_other% stays",
                ProgressionPlaceholders.inject("%player% is %level% and %some_other% stays", "level", 5)
        );
    }

    @Test
    void aResolvedValueIsNeverTreatedAsARegexReplacement() {
        // Guards the appendReplacement call: a $ or \ reaching it unquoted would throw.
        assertEquals("cost $5", ProgressionPlaceholders.inject("cost $%level%", "level", 5));
    }

    @Test
    void resolveOffsetReturnsNullForAnythingElse() {
        assertNull(ProgressionPlaceholders.resolveOffset("not_a_placeholder", "level", 5));
        assertNull(ProgressionPlaceholders.resolveOffset("level", "level", 5));
        assertNull(ProgressionPlaceholders.resolveOffset("level_", "level", 5));
    }
}
