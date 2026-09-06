package com.willfp.eco.internal.spigot.leaderboard

import com.willfp.eco.core.Eco
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.leaderboard.LeaderboardValueProvider
import java.math.BigDecimal
import java.util.UUID

/**
 * Ranks players by the value of a single numeric [PersistentDataKey].
 */
class KeyLeaderboardValueProvider(
    private val key: PersistentDataKey<*>
) : LeaderboardValueProvider {
    @Suppress("UNCHECKED_CAST")
    override fun readValues(uuids: Set<UUID>): Map<UUID, Double> {
        // Read in bulk rather than through PlayerProfile on purpose. PlayerProfile.load(uuid)
        // inserts into ProfileHandler's loaded-profile map, which is only cleared when a player
        // quits, so loading a profile per uuid across the whole playerbase would retain one
        // EcoProfile per uuid for the lifetime of the server. readAllProfileValues loads and
        // retains nothing.
        val raw = Eco.get().readAllProfileValues(uuids, key as PersistentDataKey<Any>)

        val values = HashMap<UUID, Double>(raw.size)

        for ((uuid, value) in raw) {
            val asDouble = when (value) {
                is Int -> value.toDouble()
                is Double -> value
                is BigDecimal -> value.toDouble()
                is Number -> value.toDouble()
                // Strings, booleans, and anything else are not rankable, so the player is left
                // unranked rather than being ranked as zero.
                else -> null
            }

            if (asDouble != null) {
                values[uuid] = asDouble
            }
        }

        return values
    }
}
