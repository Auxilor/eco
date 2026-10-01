package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.internal.spigot.data.KeyRegistry
import com.willfp.eco.internal.spigot.data.handlers.impl.RedisPersistentDataHandler
import java.util.UUID
import java.util.logging.Level
import java.util.logging.Logger
import org.bukkit.NamespacedKey
import redis.clients.jedis.JedisPubSub

/**
 * The profiles that cross-server sync keeps fresh.
 */
interface SyncedProfiles {
    /**
     * Drop the cached value of [key] for [uuid], if that profile is loaded.
     */
    fun invalidate(uuid: UUID, key: PersistentDataKey<*>)

    /**
     * Drop every cached value shared with other servers, on every loaded profile.
     */
    fun invalidateAllShared()

    /**
     * Whether this server has a value for [key] waiting to be saved, which is newer than the one
     * another server just stored.
     */
    fun isPending(uuid: UUID, key: PersistentDataKey<*>): Boolean

    /**
     * Called with the value another server stored, so that anything watching writes - the
     * leaderboards - sees changes made anywhere on the network.
     */
    fun onRemoteWrite(uuid: UUID, key: PersistentDataKey<*>, value: Any)
}

/**
 * Keeps cached profile data fresh across servers sharing one Redis.
 *
 * Every write the handler commits is announced in the same transaction, so a server that hears the
 * announcement always reads the new value. Pub/sub does not queue messages for a subscriber that
 * is not connected, so after reconnecting every shared cached value is dropped: anything could
 * have changed in the gap.
 *
 * Two servers writing the same key at once still resolve by whichever saves last. This keeps reads
 * fresh; it does not merge concurrent changes.
 */
class RedisProfileSync(
    private val redis: RedisPersistentDataHandler,
    private val serverId: String,
    private val profiles: SyncedProfiles,
    private val logger: Logger,
    private val initialBackoffMillis: Long = 1_000,
    private val maxBackoffMillis: Long = 30_000
) {
    @Volatile
    private var running = false

    @Volatile
    private var subscriber: Subscriber? = null

    private var thread: Thread? = null

    /**
     * Whether the subscription is currently connected.
     */
    @Volatile
    var isSubscribed = false
        private set

    fun start() {
        check(!running) { "Sync is already running" }

        running = true
        redis.syncSender = serverId

        thread = Thread(::run, "eco-redis-sync").apply {
            isDaemon = true
            start()
        }
    }

    fun stop() {
        if (!running) {
            return
        }

        running = false
        redis.syncSender = null

        runCatching { subscriber?.takeIf { it.isSubscribed }?.unsubscribe() }

        thread?.interrupt()
        thread?.join(STOP_TIMEOUT_MILLIS)
        thread = null
        isSubscribed = false
    }

    private fun run() {
        var backoff = initialBackoffMillis
        var connectedBefore = false
        var warned = false

        while (running) {
            val subscriber = Subscriber {
                // Anything could have changed while no messages were arriving.
                if (connectedBefore) {
                    profiles.invalidateAllShared()
                }

                if (warned) {
                    logger.info("Reconnected to Redis for cross-server sync")
                }

                connectedBefore = true
                warned = false
                backoff = initialBackoffMillis
            }

            this.subscriber = subscriber

            try {
                redis.withConnection { it.subscribe(subscriber, redis.syncChannel) }
            } catch (e: Exception) {
                if (!running) {
                    break
                }

                if (!warned) {
                    logger.log(Level.WARNING, "Lost connection to Redis for cross-server sync, retrying", e)
                    warned = true
                }
            } finally {
                isSubscribed = false
            }

            if (!running) {
                break
            }

            try {
                Thread.sleep(backoff)
            } catch (_: InterruptedException) {
                break
            }

            backoff = (backoff * 2).coerceAtMost(maxBackoffMillis)
        }
    }

    private fun handle(message: String) {
        val parts = message.split('|', limit = 3)

        if (parts.size != 3 || parts[0] == serverId) {
            return
        }

        val uuid = runCatching { UUID.fromString(parts[1]) }.getOrNull() ?: return
        val namespacedKey = NamespacedKey.fromString(parts[2]) ?: return
        val key = KeyRegistry.getKey(namespacedKey) ?: return

        // The value waiting here is newer, and overwrites the one just stored when it is saved.
        if (profiles.isPending(uuid, key)) {
            return
        }

        profiles.invalidate(uuid, key)

        val value = redis.read(uuid, key) ?: return
        profiles.onRemoteWrite(uuid, key, value)
    }

    private inner class Subscriber(
        private val onConnected: () -> Unit
    ) : JedisPubSub() {
        override fun onSubscribe(channel: String, subscribedChannels: Int) {
            this@RedisProfileSync.isSubscribed = true
            onConnected()
        }

        override fun onMessage(channel: String, message: String) {
            // One bad message must not end the subscription.
            try {
                handle(message)
            } catch (e: Exception) {
                logger.log(Level.WARNING, "Failed to apply a cross-server sync message", e)
            }
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
