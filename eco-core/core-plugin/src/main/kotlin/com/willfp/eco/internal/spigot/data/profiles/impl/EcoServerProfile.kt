package com.willfp.eco.internal.spigot.data.profiles.impl

import com.willfp.eco.core.data.ServerProfile
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.data.profiles.ProfileHandler
import com.willfp.eco.util.namespacedKeyOf
import java.util.UUID

val serverIDKey = PersistentDataKey(
    namespacedKeyOf("eco", "server_id"),
    PersistentDataKeyType.STRING,
    ""
)

val localServerIDKey = PersistentDataKey(
    namespacedKeyOf("eco", "local_server_id"),
    PersistentDataKeyType.STRING,
    "",
    true
)

val serverProfileUUID = UUID(0, 0)

class EcoServerProfile(
    handler: ProfileHandler
) : EcoProfile(serverProfileUUID, handler), ServerProfile {
    override fun getServerID(): String {
        if (this.read(serverIDKey).isBlank()) {
            this.write(serverIDKey, UUID.randomUUID().toString())
        }

        return this.read(serverIDKey)
    }

    override fun getLocalServerID(): String {
        if (this.read(localServerIDKey).isBlank()) {
            this.write(localServerIDKey, UUID.randomUUID().toString())
        }

        return this.read(localServerIDKey)
    }

    override fun toString(): String {
        return "EcoServerProfile"
    }
}
