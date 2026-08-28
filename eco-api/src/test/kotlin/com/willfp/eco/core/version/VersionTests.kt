package com.willfp.eco.core.version

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests for [Version.isAtLeast].
 *
 * Qualifiers are build tags rather than versions behind, so they are ignored when checking
 * against a minimum required version, unlike in [Version.compareTo] where a qualified version
 * sorts below the same version without one.
 */
class VersionTests {
    @Test
    fun `equal versions satisfy the requirement`() {
        assertTrue(Version("2026.34").isAtLeast(Version("2026.34")))
    }

    @Test
    fun `newer versions satisfy the requirement`() {
        assertTrue(Version("2026.35").isAtLeast(Version("2026.34")))
        assertTrue(Version("2027.1").isAtLeast(Version("2026.34")))
    }

    @Test
    fun `older versions do not satisfy the requirement`() {
        assertFalse(Version("2026.33").isAtLeast(Version("2026.34")))
        assertFalse(Version("2025.99").isAtLeast(Version("2026.34")))
    }

    @Test
    fun `qualified versions satisfy a requirement of the same version`() {
        assertTrue(Version("2026.34-local").isAtLeast(Version("2026.34")))
        assertTrue(Version("2026.34-SNAPSHOT").isAtLeast(Version("2026.34")))
    }

    @Test
    fun `qualified versions do not satisfy a requirement of a newer version`() {
        assertFalse(Version("2026.34-local").isAtLeast(Version("2026.35")))
    }

    @Test
    fun `a qualified requirement is satisfied by the plain version`() {
        assertTrue(Version("2026.34").isAtLeast(Version("2026.34-SNAPSHOT")))
    }

    @Test
    fun `missing segments are treated as zero`() {
        assertTrue(Version("1.0").isAtLeast(Version("1.0.0")))
        assertTrue(Version("1.0.0").isAtLeast(Version("1.0")))
        assertFalse(Version("1.0").isAtLeast(Version("1.0.1")))
    }

    @Test
    fun `comparison ordering still ranks qualifiers below plain versions`() {
        assertTrue(Version("2026.34-local") < Version("2026.34"))
    }
}
