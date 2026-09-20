package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.data.keys.PersistentDataKey
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Carries profiles out of [source] one at a time, while the server runs.
 *
 * A profile lives in exactly one place at a time, and this is what knows which: a profile the
 * target already holds a row for is migrated and is read and written there, and a profile it holds
 * nothing for is copied across the first time anything asks for it. Nothing is locked, nothing is
 * restarted, and a profile is copied once.
 *
 * The window in which a profile is being copied is the only time two stores are live for it at
 * once. Writes made during that window go to both (see [isMigrating] and [noteWrite]) and the
 * copy skips the keys they touched, so a live write is never overwritten by the older value the
 * copy is carrying.
 *
 * [pause] is called between profiles on the sweep, and is how a server keeps the migration from
 * being the heaviest thing on its database while it runs. Nothing waits on the sweep - a profile
 * that is asked for is carried across then and there - so it can afford to be slow.
 */
class LiveProfileMigration(
    private val source: PersistentDataHandler,
    private val keys: () -> Set<PersistentDataKey<*>>,
    private val targetFor: (PersistentDataKey<*>) -> PersistentDataHandler,
    private val log: (String) -> Unit,
    private val pause: () -> Unit = {}
) {
    private val migrated = ConcurrentHashMap.newKeySet<UUID>()
    private val migrating = ConcurrentHashMap<UUID, MutableSet<PersistentDataKey<*>>>()
    private val writtenTo = ConcurrentHashMap.newKeySet<UUID>()

    // Profiles the target already held a row for, and so were not copied. These are the only ones
    // a half-finished copy from an earlier boot can be hiding in, and so the only ones [verify]
    // has anything to look at -- see there.
    private val preexisting = ConcurrentHashMap.newKeySet<UUID>()

    // One lock per profile being resolved, so two threads asking for the same profile copy it
    // once, and a profile is never held up by an unrelated one being copied.
    private val locks = ConcurrentHashMap<UUID, Any>()

    /**
     * Whether every profile the source holds has been carried across.
     */
    @Volatile
    var isComplete = false
        private set

    /**
     * Whether [uuid] is being copied right now, and so has two live stores.
     */
    fun isMigrating(uuid: UUID): Boolean =
        migrating.containsKey(uuid)

    /**
     * Record that the server itself wrote [key] for [uuid] while the migration was running.
     *
     * A profile being copied right now has the key skipped by the copy, leaving the value the
     * server just wrote in place: it is newer than anything the source holds.
     *
     * Every profile written to is remembered for [verify], which is the part that cannot tell a
     * key the copy never reached from one the server has since cleared. Only the uuid is kept, and
     * the whole profile is left out of verification: a profile the server has touched is the
     * server's, and data.yml has nothing to tell it.
     */
    fun noteWrite(uuid: UUID, key: PersistentDataKey<*>) {
        writtenTo.add(uuid)
        migrating[uuid]?.add(key)
    }

    /**
     * Carry [uuid] across if it has not been already.
     *
     * Blocks the calling thread for the length of the copy, so it belongs off the main thread -
     * on login, or on the sweep - and is a set lookup for a profile that has already been seen.
     */
    fun ensureMigrated(uuid: UUID) {
        ensureMigrated(uuid, throttle = false)
    }

    private fun ensureMigrated(uuid: UUID, throttle: Boolean) {
        if (uuid in migrated) {
            return
        }

        val lock = locks.computeIfAbsent(uuid) { Any() }

        val copied = synchronized(lock) {
            if (uuid in migrated) {
                return@synchronized false
            }

            try {
                // Claimed before the target is asked anything, so a write landing between the
                // check and the copy is recorded rather than lost.
                val written = ConcurrentHashMap.newKeySet<PersistentDataKey<*>>()
                migrating[uuid] = written

                val holdsProfile = targetHoldsProfile(uuid)

                if (holdsProfile) {
                    preexisting.add(uuid)
                } else {
                    copy(uuid, written)
                }

                migrated.add(uuid)

                !holdsProfile
            } finally {
                migrating.remove(uuid)
                locks.remove(uuid)
            }
        }

        // Outside the lock, and only for a profile this call actually carried: a profile someone
        // else is waiting on must never be held by the throttle, and a profile the target already
        // holds costs a lookup rather than a copy. A server that restarts near the end of a sweep
        // would otherwise sleep its way through every profile the last boot already carried.
        if (throttle && copied) {
            pause()
        }
    }

    /**
     * Carry across every profile the source holds, one at a time.
     *
     * Profiles resolved by a login on the way through are already in [migrated] and cost a set
     * lookup each, so a server that is busy while this runs does less work here, not more.
     */
    fun sweep() {
        val uuids = source.getSavedUUIDs()

        log("Migrating ${uuids.size} profiles out of ${source.id} in the background")

        var done = 0
        var reported = 0

        for (uuid in uuids) {
            ensureMigrated(uuid, throttle = true)

            done++

            // Reported by tenth rather than by profile, so the number of lines a migration writes
            // is the same whether the server has a thousand profiles or a million. The last one is
            // left out: the finishing line below already says the sweep is done.
            val milestone = (done * 100 / uuids.size) / 10 * 10

            if (milestone > reported && done < uuids.size) {
                reported = milestone

                log("Migrated $milestone% of profiles ($done/${uuids.size})")
            }
        }

        isComplete = true

        log("Finished migrating profiles out of ${source.id}")
    }

    /**
     * Carry across anything the sweep left behind, and answer with how much that was.
     *
     * A profile is marked as carried the moment the target holds anything at all for it, so a boot
     * that died part-way through one profile leaves the rest of its keys in the source and the
     * next sweep skips it. This is the pass that catches those: for every profile the sweep
     * skipped, every key the target has no value for is written across.
     *
     * Only the skipped profiles are looked at. A profile this run copied was copied whole - the
     * copy either finished or threw - so sweeping it again is a pass over the entire playerbase,
     * times every key registered on the server, to find nothing. On a server with thousands of
     * generated keys that is the single longest thing the migration does, and it holds the
     * leaderboards paused for the whole of it.
     *
     * Profiles the server wrote to while the migration ran are left out entirely - see [noteWrite].
     * A value missing from the target is only evidence of a half-finished copy on a profile
     * nothing else has touched; on one the server has been writing to, it is just as likely to be
     * a list the player has emptied, and restoring it from data.yml would undo that.
     *
     * Belongs after [sweep], off the main thread, and before the migration is marked finished.
     */
    fun verify(chunkSize: Int = VERIFY_CHUNK_SIZE): Int {
        val uuids = preexisting - writtenTo

        if (uuids.isEmpty()) {
            return 0
        }

        val keys = keys().toList()

        if (keys.isEmpty()) {
            return 0
        }

        log("Checking ${uuids.size} profiles the sweep skipped against ${keys.size} keys")

        var repaired = 0
        var done = 0
        var reported = 0

        for (key in keys) {
            // One key at a time rather than a batch per target: the pass is the longest part of
            // the migration on a server with thousands of keys, and a key is the only unit of it
            // that finishes often enough to report on.
            //
            // Repaired quietly: the per-key logging belongs to a backfill an operator asked for,
            // and this is the migration finishing its own work.
            repaired += backfillProfiles(source, targetFor(key), setOf(key), { }, uuids, chunkSize, pause)

            done++

            // By tenth, as the sweep reports, and with the last key left to the finishing line.
            val milestone = (done * 100 / keys.size) / 10 * 10

            if (milestone > reported && done < keys.size) {
                reported = milestone

                log("Checked $milestone% of keys ($done/${keys.size})")
            }
        }

        log("Finished checking the profiles the sweep skipped")

        return repaired
    }

    private fun targetHoldsProfile(uuid: UUID): Boolean {
        // Distinct because the local and configured handlers are the same instance on a server
        // whose configured handler is sqlite, and asking it twice doubles the lookups.
        val targets = keys().mapTo(mutableSetOf()) { targetFor(it) }

        return targets.any { it.hasStoredProfile(uuid) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun copy(uuid: UUID, written: Set<PersistentDataKey<*>>) {
        for (key in keys()) {
            if (key in written) {
                continue
            }

            val typed = key as PersistentDataKey<Any>
            val value = source.read(uuid, typed) ?: continue

            // Checked again after the read, which is where the window lives: a write landing
            // while the source was being read is newer than the value just read.
            if (key in written) {
                continue
            }

            targetFor(key).write(uuid, typed, value)
        }
    }
}

private const val VERIFY_CHUNK_SIZE = 500
