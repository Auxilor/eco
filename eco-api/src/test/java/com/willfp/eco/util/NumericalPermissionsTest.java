package com.willfp.eco.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NumericalPermissionsTest {
    private static final String PREFIX = "ecoskills.xpmultiplier";

    @Test
    void takesTheHighestNotTheFirst() {
        // Permissions are unordered, so "first match" is a coin flip when a player holds two
        // through different groups. This is the whole reason the shared version exists.
        assertEquals(300.0, NumericalPermissions.highest(
                List.of(PREFIX + ".150", PREFIX + ".300"), PREFIX, 0.0));
        assertEquals(300.0, NumericalPermissions.highest(
                List.of(PREFIX + ".300", PREFIX + ".150"), PREFIX, 0.0));
    }

    @Test
    void honoursNegativeValues() {
        // The copies this replaces seeded with Double.MIN_VALUE - the smallest *positive*
        // double - so every negative grant lost to the seed and silently did nothing.
        assertEquals(-50.0, NumericalPermissions.highest(
                List.of(PREFIX + ".-50"), PREFIX, 0.0));
        assertEquals(-50.0, NumericalPermissions.highest(
                List.of(PREFIX + ".-50", PREFIX + ".-100"), PREFIX, 0.0));
    }

    @Test
    void skipsUnparseableSuffixes() {
        // A typo must contribute nothing rather than resolving to some default.
        assertEquals(0.0, NumericalPermissions.highest(
                List.of(PREFIX + ".abc"), PREFIX, 0.0));
        assertEquals(150.0, NumericalPermissions.highest(
                List.of(PREFIX + ".abc", PREFIX + ".150"), PREFIX, 0.0));
    }

    @Test
    void namedTiersDoNotCountAsNumbers() {
        // The named tiers are checked separately by each plugin and must not be read here.
        assertEquals(0.0, NumericalPermissions.highest(
                List.of(PREFIX + ".double", PREFIX + ".quadruple"), PREFIX, 0.0));
    }

    @Test
    void unrelatedPermissionsAreIgnored() {
        assertEquals(0.0, NumericalPermissions.highest(
                List.of("ecojobs.xpmultiplier.500", "someplugin.other.900"), PREFIX, 0.0));
    }

    @Test
    void oneScopeDoesNotLeakIntoAnother() {
        // Nodes stay per-plugin: holding the jobs multiplier must not affect skills.
        List<String> held = List.of("ecojobs.xpmultiplier.500", "ecoskills.xpmultiplier.100");

        assertEquals(100.0, NumericalPermissions.highest(held, "ecoskills.xpmultiplier", 0.0));
        assertEquals(500.0, NumericalPermissions.highest(held, "ecojobs.xpmultiplier", 0.0));
    }

    @Test
    void defaultIsUsedWhenNothingMatches() {
        assertEquals(42.0, NumericalPermissions.highest(List.of(), PREFIX, 42.0));
        assertEquals(42.0, NumericalPermissions.highest(List.of("unrelated.node"), PREFIX, 42.0));
    }

    @Test
    void zeroIsAValueNotAnAbsence() {
        assertEquals(0.0, NumericalPermissions.highest(List.of(PREFIX + ".0"), PREFIX, 99.0));
    }
}
