package com.willfp.eco.internal.spigot.leaderboard

import com.willfp.eco.core.Eco
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.leaderboard.LeaderboardValueProvider
import java.util.UUID

// The only key types that can ever produce a number to rank by. A key of any other type would
// register happily, query the database on every refresh, and rank nobody, forever.
private val RANKABLE_KEY_TYPES = setOf(
    PersistentDataKeyType.INT,
    PersistentDataKeyType.DOUBLE,
    PersistentDataKeyType.BIG_DECIMAL
)

/**
 * Ranks players by the value of a single numeric [PersistentDataKey].
 */
class KeyLeaderboardValueProvider(
    private val key: PersistentDataKey<*>
) : LeaderboardValueProvider {
    init {
        // Rejected at registration time rather than silently producing an empty leaderboard:
        // the type is known here, and the stack trace points at the exact call site.
        require(key.type in RANKABLE_KEY_TYPES) {
            "Cannot rank by the key '${key.key}': a key of type ${key.type.name()} never yields " +
                    "a number, so the leaderboard would never rank anybody. Register a " +
                    "leaderboard with a custom LeaderboardValueProvider instead."
        }
    }

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
