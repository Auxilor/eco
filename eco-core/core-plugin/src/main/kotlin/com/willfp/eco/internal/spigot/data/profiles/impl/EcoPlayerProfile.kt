package com.willfp.eco.internal.spigot.data.profiles.impl

import com.willfp.eco.core.data.PlayerProfile
import com.willfp.eco.internal.spigot.data.profiles.ProfileHandler
import java.util.UUID

class EcoPlayerProfile(
    uuid: UUID,
    handler: ProfileHandler
) : EcoProfile(uuid, handler), PlayerProfile {
    override fun toString(): String {
        return "EcoPlayerProfile{uuid=$uuid}"
    }
}
