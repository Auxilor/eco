package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import java.util.UUID

/**
 * Writes profile values back into data.yml, at the paths the yaml handler reads them from.
 *
 * The yaml handler itself stays read-only: this exists for the one case where data.yml is still a
 * live store, which is a profile part-way through being copied out of it. If the server stops mid
 * copy, the values written here are what the next boot resumes from.
 */
class DataYmlProfileStore(
    private val dataYml: Config
) {
    fun <T : Any> write(uuid: UUID, key: PersistentDataKey<T>, value: T) {
        dataYml.set("player.$uuid.${key.key}", value)
    }
}
