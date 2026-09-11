package com.willfp.eco.internal.spigot.leaderboard

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * The ranked value of every tracked player for one leaderboard, held in memory.
 *
 * Written by two things only: the write hook, on every value change this server makes, and
 * reconciliation, which replaces the whole map from the database. Reads go through [snapshot],
 * which copies, so a sort can never observe a half-applied reconcile.
 *
 * The representation is deliberately a plain map. On a very large server an interned uuid index
 * with a flat double array would use roughly a fifth of the heap, but nothing outside this class
 * can observe the difference, so that optimisation can be made later without an API change.
 */
class PlayerbaseValues {
    private val values = ConcurrentHashMap<UUID, Double>()

    private val dirty = AtomicBoolean(false)

    // The uuids written while a reconcile is in flight, or null when none is. Allocated only for
    // the duration of one, so the steady-state write path allocates nothing.
    @Volatile
    private var overlay: MutableSet<UUID>? = null

    /** Whether anything has changed since the last sort published a snapshot. */
    val isDirty: Boolean
        get() = dirty.get()

    fun clearDirty() {
        dirty.set(false)
    }

    /**
     * Record a value change.
     *
     * Called from the profile writer's per-tick drain, so this must stay a map put and a flag set:
     * no I/O, and no allocation outside of a reconcile.
     */
    fun put(uuid: UUID, value: Double) {
        values[uuid] = value
        overlay?.add(uuid)
        dirty.set(true)
    }

    /**
     * Record that a player is no longer ranked.
     *
     * Dropping back to the key's default is a value change like any other, so it has to be
     * recorded in the overlay too -- otherwise a reconcile that read the player's old value would
     * put them back on the leaderboard.
     */
    fun remove(uuid: UUID) {
        values.remove(uuid)
        overlay?.add(uuid)
        dirty.set(true)
    }

    fun snapshot(): Map<UUID, Double> = HashMap(values)

    /**
     * Begin a reconcile.
     *
     * From this point every write is also recorded in an overlay, so that [installReconciled] can
     * tell which uuids hold a value newer than the one the database read returned.
     */
    fun beginReconcile() {
        overlay = ConcurrentHashMap.newKeySet()
    }

    /**
     * Replace every value with the reconciled set, keeping any uuid written while the reconcile
     * was in flight.
     *
     * A write landing between the database read and this call is newer than what the read
     * returned, so replacing wholesale would silently revert it. Those uuids keep their in-memory
     * value instead -- including having been removed, which must not be undone by the read.
     *
     * If more than [maxOverlay] uuids were written during the reconcile, its results are treated
     * as superseded and discarded outright: a read that stale is not worth merging, and the next
     * reconcile will catch up.
     *
     * @return Whether the reconciled values were installed.
     */
    fun installReconciled(reconciled: Map<UUID, Double>, maxOverlay: Int): Boolean {
        val overlay = this.overlay ?: return false
        this.overlay = null

        if (overlay.size > maxOverlay) {
            return false
        }

        // Captured before the swap: these are the values that are newer than the read.
        val newer = HashMap<UUID, Double>(overlay.size)
        for (uuid in overlay) {
            values[uuid]?.let { newer[uuid] = it }
        }

        values.clear()
        values.putAll(reconciled)
        values.putAll(newer)

        // A uuid written during the reconcile that no longer has a value was removed while the
        // read was in flight, so the read must not resurrect it.
        for (uuid in overlay) {
            if (uuid !in newer) {
                values.remove(uuid)
            }
        }

        dirty.set(true)

        return true
    }

    /**
     * Abandon a reconcile without installing anything.
     *
     * Used when the database read failed: the values already in memory are the best available, so
     * they are kept untouched. Without this the overlay would stay open and keep accumulating
     * every write until the next reconcile happened to close it.
     */
    fun abortReconcile() {
        overlay = null
    }

    fun clear() {
        values.clear()
        overlay = null
        dirty.set(true)
    }
}
