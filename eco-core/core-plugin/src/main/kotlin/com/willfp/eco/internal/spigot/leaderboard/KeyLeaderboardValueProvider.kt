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
 *
 * A player is ranked only if their stored value is strictly greater than the key's default -- in
 * other words, only if they have made progress. Players with no stored value, and players sitting
 * on the default, are left unranked and excluded from the tracked-player count, so percentiles are
 * computed over the progressed population rather than over everyone who has ever joined.
 *
 * The default is used as the threshold rather than zero because defaults are not uniformly zero:
 * a skill starts at its start level, a job at 1 when unlocked by default, and a currency at
 * whatever the server configured. Filtering on zero would exclude nobody at all from most of them.
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

    /**
     * The key being ranked.
     *
     * Exposed so the refresh sweep can read every ranked key on the server in one batched query
     * and hand each provider its own slice, rather than having each one issue a read of its own.
     */
    val rankedKey: PersistentDataKey<*>
        get() = key

    // Every rankable type is a Number, so this cannot fail for a key that passed the check above.
    private val default = (key.defaultValue as Number).toDouble()

    @Suppress("UNCHECKED_CAST")
    override fun readValues(uuids: Set<UUID>): Map<UUID, Double> {
        // Read in bulk rather than through PlayerProfile on purpose. PlayerProfile.load(uuid)
        // inserts into ProfileHandler's loaded-profile map, which is only cleared when a player
        // quits, so loading a profile per uuid across the whole playerbase would retain one
        // EcoProfile per uuid for the lifetime of the server. readAllProfileValues loads and
        // retains nothing.
        return convert(Eco.get().readAllProfileValues(uuids, key as PersistentDataKey<Any>))
    }

    /**
     * Apply the no-progress rule to values that have already been read.
     *
     * Used by the refresh sweep, which reads every ranked key in one batched query and then hands
     * each provider its own slice, and by the incremental write hook, so that a single write is
     * filtered by exactly the same rule as a full read.
     */
    fun convert(raw: Map<UUID, Any>): Map<UUID, Double> {
        val values = HashMap<UUID, Double>(raw.size)

        for ((uuid, value) in raw) {
            // Strings, booleans, and anything else are not rankable, so the player is left
            // unranked rather than being ranked as zero.
            val asDouble = (value as? Number)?.toDouble() ?: continue

            // Strictly greater: a player sitting on the default has made no progress, and ranking
            // them would pad every leaderboard with the entire playerbase.
            if (asDouble > default) {
                values[uuid] = asDouble
            }
        }

        return values
    }
}
