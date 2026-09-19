package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.data.keys.PersistentDataKey

/**
 * Copy every profile [from] holds into [to], for [keys] only.
 *
 * Free of the plugin so that it can be tested against a real pair of handlers; the logging is a
 * callback for the same reason.
 */
fun migrateProfiles(
    from: PersistentDataHandler,
    to: PersistentDataHandler,
    keys: Set<PersistentDataKey<*>>,
    log: (String) -> Unit
) {
    log("Keys to migrate: ${keys.map { it.key }.joinToString(", ")}")
    log("Loading profile UUIDs from ${from.id}...")
    log("This step may take a while depending on the size of your database.")

    val uuids = from.getSavedUUIDs()

    log("Found ${uuids.size} profiles to migrate")

    for ((index, uuid) in uuids.withIndex()) {
        log("(${index + 1}/${uuids.size}) Migrating $uuid")
        to.loadSerializedProfile(from.serializeProfile(uuid, keys))
    }
}
