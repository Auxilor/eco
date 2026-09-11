package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.internal.spigot.data.KeyRegistry
import java.util.UUID

/**
 * A handler that can answer "do you hold anything at all for this profile" without reading it.
 *
 * The live migration asks this of every profile it has not seen before, so a handler backed by a
 * database answers it with an indexed lookup rather than by deserializing the profile.
 */
interface ProfileExistenceCheck {
    fun hasStoredProfile(uuid: UUID): Boolean
}

/**
 * Whether [uuid] has anything stored in this handler.
 *
 * A handler that cannot answer cheaply is asked the slow way: read the registered keys until one
 * of them has a value. That is what the migration would have done in any case, so the fallback
 * costs a handler nothing beyond the reads it was already going to serve.
 */
fun PersistentDataHandler.hasStoredProfile(uuid: UUID): Boolean {
    if (this is ProfileExistenceCheck) {
        return this.hasStoredProfile(uuid)
    }

    return KeyRegistry.getRegisteredKeys().any { this.read(uuid, it) != null }
}
