package com.willfp.eco.internal.spigot.leaderboard

import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * The reconcile overlay is the fix for a write landing between a sweep's database read and its
 * install. Getting it wrong surfaces as one player's value being silently reverted roughly once a
 * week, which is miserable to reproduce, so every branch of it is pinned here.
 */
class PlayerbaseValuesTests {
    private val alice: UUID = UUID.randomUUID()
    private val bob: UUID = UUID.randomUUID()

    @Test
    fun `a put is visible in the snapshot`() {
        val values = PlayerbaseValues()

        values.put(alice, 5.0)

        assertEquals(mapOf(alice to 5.0), values.snapshot())
    }

    @Test
    fun `a put marks the values dirty`() {
        val values = PlayerbaseValues()

        assertFalse(values.isDirty)

        values.put(alice, 5.0)

        assertTrue(values.isDirty)
    }

    @Test
    fun `clearing the dirty flag holds until the next write`() {
        val values = PlayerbaseValues()
        values.put(alice, 5.0)

        values.clearDirty()

        assertFalse(values.isDirty)
    }

    @Test
    fun `a removal is a change and marks the values dirty`() {
        val values = PlayerbaseValues()
        values.put(alice, 5.0)
        values.clearDirty()

        values.remove(alice)

        assertTrue(values.isDirty)
        assertNull(values.snapshot()[alice])
    }

    @Test
    fun `the snapshot is a copy, so a sort cannot observe a later write`() {
        val values = PlayerbaseValues()
        values.put(alice, 5.0)

        val snapshot = values.snapshot()
        values.put(bob, 9.0)

        assertEquals(mapOf(alice to 5.0), snapshot)
    }

    @Test
    fun `a reconcile with no concurrent writes replaces everything`() {
        val values = PlayerbaseValues()
        values.put(alice, 5.0)

        values.beginReconcile()

        assertTrue(values.installReconciled(mapOf(bob to 9.0), 1000))
        assertEquals(mapOf(bob to 9.0), values.snapshot())
    }

    @Test
    fun `a write during a reconcile survives the install`() {
        val values = PlayerbaseValues()

        values.beginReconcile()

        // Lands after the sweep read the database, but before it installs.
        values.put(alice, 42.0)

        assertTrue(values.installReconciled(mapOf(alice to 5.0, bob to 9.0), 1000))

        assertEquals(42.0, values.snapshot()[alice], "the newer in-memory write must win")
        assertEquals(9.0, values.snapshot()[bob], "untouched uuids come from the read")
    }

    @Test
    fun `a removal during a reconcile is not undone by the install`() {
        val values = PlayerbaseValues()
        values.put(alice, 42.0)

        values.beginReconcile()

        // The player dropped back to the key's default while the sweep was reading.
        values.remove(alice)

        assertTrue(values.installReconciled(mapOf(alice to 42.0), 1000))
        assertNull(values.snapshot()[alice], "the read must not resurrect a removed player")
    }

    @Test
    fun `an oversized overlay discards the reconcile entirely`() {
        val values = PlayerbaseValues()

        values.beginReconcile()

        for (i in 1..5) {
            values.put(UUID.randomUUID(), i.toDouble())
        }

        assertFalse(values.installReconciled(mapOf(bob to 9.0), 4))
        assertFalse(bob in values.snapshot(), "nothing from a discarded sweep may land")
    }

    @Test
    fun `installing without beginning a reconcile changes nothing`() {
        val values = PlayerbaseValues()
        values.put(alice, 5.0)

        assertFalse(values.installReconciled(mapOf(bob to 9.0), 1000))
        assertEquals(mapOf(alice to 5.0), values.snapshot())
    }

    @Test
    fun `installing marks the values dirty so the next sort tick publishes`() {
        val values = PlayerbaseValues()
        values.clearDirty()

        values.beginReconcile()
        values.installReconciled(mapOf(bob to 9.0), 1000)

        assertTrue(values.isDirty)
    }

    @Test
    fun `clear empties the values and abandons any reconcile in flight`() {
        val values = PlayerbaseValues()
        values.put(alice, 5.0)
        values.beginReconcile()

        values.clear()

        assertTrue(values.snapshot().isEmpty())
        assertFalse(values.installReconciled(mapOf(bob to 9.0), 1000))
    }
}
