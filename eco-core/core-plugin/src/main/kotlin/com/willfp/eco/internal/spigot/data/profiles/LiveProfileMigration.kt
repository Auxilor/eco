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
 * once. Writes made during that window go to both (see [isMigrating] and [noteLiveWrite]) and the
 * copy skips the keys they touched, so a live write is never overwritten by the older value the
 * copy is carrying.
 */
class LiveProfileMigration(
    private val source: PersistentDataHandler,
    private val keys: () -> Set<PersistentDataKey<*>>,
    private val targetFor: (PersistentDataKey<*>) -> PersistentDataHandler,
    private val log: (String) -> Unit
) {
    private val migrated = ConcurrentHashMap.newKeySet<UUID>()
    private val migrating = ConcurrentHashMap<UUID, MutableSet<PersistentDataKey<*>>>()

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
     * Record that the server itself wrote [key] for [uuid] while the profile was being copied.
     *
     * The copy skips the key, leaving the value the server just wrote in place: it is newer than
     * anything the source holds.
     */
    fun noteLiveWrite(uuid: UUID, key: PersistentDataKey<*>) {
        migrating[uuid]?.add(key)
    }

    /**
     * Carry [uuid] across if it has not been already.
     *
     * Blocks the calling thread for the length of the copy, so it belongs off the main thread -
     * on login, or on the sweep - and is a set lookup for a profile that has already been seen.
     */
    fun ensureMigrated(uuid: UUID) {
        if (uuid in migrated) {
            return
        }

        val lock = locks.computeIfAbsent(uuid) { Any() }

        synchronized(lock) {
            if (uuid in migrated) {
                return
            }

            try {
                // Claimed before the target is asked anything, so a write landing between the
                // check and the copy is recorded rather than lost.
                val written = ConcurrentHashMap.newKeySet<PersistentDataKey<*>>()
                migrating[uuid] = written

                if (!targetHoldsProfile(uuid)) {
                    copy(uuid, written)
                }

                migrated.add(uuid)
            } finally {
                migrating.remove(uuid)
                locks.remove(uuid)
            }
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

        for (uuid in uuids) {
            ensureMigrated(uuid)
        }

        isComplete = true

        log("Finished migrating profiles out of ${source.id}")
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
