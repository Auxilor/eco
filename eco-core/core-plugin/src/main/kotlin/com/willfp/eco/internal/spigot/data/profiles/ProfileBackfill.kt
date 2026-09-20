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
 * A pass is made one chunk of profiles at a time: reading a key for every profile at once means
 * holding the whole playerbase's values for it in memory, which is the largest thing either this
 * or the migration ever does. [pause] is called between chunks that reached the target, as the
 * migration's sweep does -- a key the source holds nothing for never touches the database, and
 * throttling it would spend the whole pass asleep on a server with thousands of registered keys
 * and data.yml values for a handful of them.
 *
 * @return The number of values written.
 */
fun backfillProfiles(
    from: PersistentDataHandler,
    to: PersistentDataHandler,
    keys: Set<PersistentDataKey<*>>,
    log: (String) -> Unit,
    uuids: Set<UUID> = from.getSavedUUIDs(),
    chunkSize: Int = DEFAULT_CHUNK_SIZE,
    pause: () -> Unit = {}
): Int {
    if (keys.isEmpty() || uuids.isEmpty()) {
        return 0
    }

    val chunks = uuids.chunked(chunkSize)

    var written = 0

    for (key in keys) {
        var forKey = 0

        for ((index, chunk) in chunks.withIndex()) {
            val result = backfillKey(from, to, key, chunk.toSet())

            forKey += result.written

            if (result.readTarget && index < chunks.size - 1) {
                pause()
            }
        }

        if (forKey > 0) {
            log("Backfilled $forKey values for ${key.key} from ${from.id} into ${to.id}")
        }

        written += forKey
    }

    return written
}

private const val DEFAULT_CHUNK_SIZE = 500

@Suppress("UNCHECKED_CAST")
private fun backfillKey(
    from: PersistentDataHandler,
    to: PersistentDataHandler,
    key: PersistentDataKey<*>,
    uuids: Set<UUID>
): ChunkResult {
    val typed = key as PersistentDataKey<Any>

    val stored = from.readAll(uuids, typed)

    if (stored.isEmpty()) {
        return ChunkResult(0, readTarget = false)
    }

    val existing = to.readAll(stored.keys, typed)
    val missing = stored.filterKeys { it !in existing }

    for ((uuid, value) in missing) {
        to.write(uuid, typed, value)
    }

    return ChunkResult(missing.size, readTarget = true)
}

/**
 * What one chunk of one key did: how much was written, and whether the target was read at all.
 *
 * The second is what the throttle is for. A chunk the source held nothing for is a pass over a
 * map in memory, and pausing after it throttles nothing.
 */
private class ChunkResult(
    val written: Int,
    val readTarget: Boolean
)
