package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.data.keys.PersistentDataKey
import java.util.UUID

/**
 * Copy the data [from] still holds for [keys] into [to], for profiles that have no value there yet.
 *
 * The migration carries the keys that were registered when it ran, and a key registers the first
 * time something constructs it - a plugin that enables late, or is reinstalled a month later,
 * registers its keys long after the one migration is over. Its data is still sitting in data.yml,
 * so it is carried across the first time the key is seen instead of being lost with it.
 *
 * A profile that already has a value in [to] is left alone: that value is newer than anything
 * data.yml holds, since data.yml stopped being written to when the migration ran.
 *
 * @return The number of values written.
 */
fun backfillProfiles(
    from: PersistentDataHandler,
    to: PersistentDataHandler,
    keys: Set<PersistentDataKey<*>>,
    log: (String) -> Unit
): Int {
    if (keys.isEmpty()) {
        return 0
    }

    val uuids = from.getSavedUUIDs()

    if (uuids.isEmpty()) {
        return 0
    }

    var written = 0

    for (key in keys) {
        val forKey = backfillKey(from, to, key, uuids)

        if (forKey > 0) {
            log("Backfilled $forKey values for ${key.key} from ${from.id} into ${to.id}")
        }

        written += forKey
    }

    return written
}

@Suppress("UNCHECKED_CAST")
private fun backfillKey(
    from: PersistentDataHandler,
    to: PersistentDataHandler,
    key: PersistentDataKey<*>,
    uuids: Set<UUID>
): Int {
    val typed = key as PersistentDataKey<Any>

    val stored = from.readAll(uuids, typed)

    if (stored.isEmpty()) {
        return 0
    }

    val existing = to.readAll(stored.keys, typed)
    val missing = stored.filterKeys { it !in existing }

    for ((uuid, value) in missing) {
        to.write(uuid, typed, value)
    }

    return missing.size
}
