package com.willfp.eco.internal.spigot.data.handlers.impl

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.Configs
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.readConfig
import com.willfp.eco.core.data.handlers.DataTypeSerializer
import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.data.profiles.ProfileExistenceCheck
import java.math.BigDecimal
import java.util.UUID
import java.util.logging.Level
import java.util.logging.Logger
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.DefaultJedisClientConfig
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisSentinelPool
import redis.clients.jedis.Response
import redis.clients.jedis.util.Pool

/**
 * Stores every profile as one Redis hash, `{prefix}profile:{uuid}`, with a field per key.
 *
 * The UUIDs with saved data are indexed in a set, `{prefix}uuids`, so that the leaderboard's scan
 * is one SMEMBERS rather than a SCAN over the whole keyspace, and never decodes a value. Every
 * value is stored as a string, so that any server on the network - or an operator in redis-cli -
 * reads the same thing.
 */
class RedisPersistentDataHandler(
    private val pool: Pool<Jedis>,
    prefix: String,
    threads: Int
) : PersistentDataHandler("redis", threads), ProfileExistenceCheck {
    private val profilePrefix = "${prefix}profile:"
    private val indexKey = "${prefix}uuids"

    /**
     * The channel that writes are announced on while sync is running.
     */
    val syncChannel = "${prefix}sync"

    /**
     * The id this server announces its writes under, or null while sync is off.
     *
     * Set by the profile sync once it knows the server's id. The announcement is published in the
     * same transaction as the write, so a server that hears it always reads the new value.
     */
    @Volatile
    var syncSender: String? = null

    private val logger = Logger.getLogger("eco")
    private val gson = Gson()

    init {
        registerSerializer(PersistentDataKeyType.STRING, { it }, { it })
        registerSerializer(PersistentDataKeyType.BOOLEAN, { it.toString() }, { it.toBooleanStrict() })
        registerSerializer(PersistentDataKeyType.INT, { it.toString() }, { it.toInt() })
        registerSerializer(PersistentDataKeyType.DOUBLE, { it.toString() }, { it.toDouble() })
        registerSerializer(PersistentDataKeyType.BIG_DECIMAL, { it.toPlainString() }, { BigDecimal(it) })

        // An empty list is stored as no value at all, as the SQL handlers store it as no rows: the
        // base contract has readAll omit it, and every handler has to agree on that.
        registerSerializer(
            PersistentDataKeyType.STRING_LIST,
            { if (it.isEmpty()) null else gson.toJson(it) },
            { stored -> JsonParser.parseString(stored).asJsonArray.map { it.asString } }
        )

        // The same JSON the SQL handlers store, so a value reads identically wherever it came from.
        registerSerializer(
            PersistentDataKeyType.CONFIG,
            {
                if (it.type == ConfigType.JSON) {
                    it.toPlaintext()
                } else {
                    Configs.fromMap(it.toMap(), ConfigType.JSON).toPlaintext()
                }
            },
            { readConfig(it, ConfigType.JSON) }
        )

        try {
            pool.resource.use { it.ping() }
        } catch (e: Exception) {
            throw IllegalStateException("Could not connect to Redis: ${e.message}", e)
        }
    }

    /**
     * Borrow a connection for as long as [block] runs.
     */
    fun <R> withConnection(block: (Jedis) -> R): R = pool.resource.use(block)

    fun profileKey(uuid: UUID) = "$profilePrefix$uuid"

    override fun getSavedUUIDs(): Set<UUID> {
        return withConnection { jedis ->
            jedis.smembers(indexKey).mapNotNull { runCatching { UUID.fromString(it) }.getOrNull() }.toSet()
        }
    }

    override fun hasStoredProfile(uuid: UUID): Boolean {
        return withConnection { it.sismember(indexKey, uuid.toString()) }
    }

    override fun <T> readAll(uuids: Set<UUID>, key: PersistentDataKey<T>): Map<UUID, T> {
        @Suppress("UNCHECKED_CAST")
        val serializer = key.type.getSerializer(this) as RedisSerializer<Any>
        val field = key.key.toString()
        val values = HashMap<UUID, T>()

        pipelined(uuids, { pipeline, uuid -> pipeline.hget(profileKey(uuid), field) }) { uuid, stored ->
            @Suppress("UNCHECKED_CAST")
            serializer.decodeOrNull(stored, field)?.let { values[uuid] = it as T }
        }

        return values
    }

    override fun readAllKeys(
        uuids: Set<UUID>,
        keys: Collection<PersistentDataKey<*>>
    ): Map<PersistentDataKey<*>, Map<UUID, Any>> {
        val ordered = keys.distinct()
        val values = ordered.associateWith { HashMap<UUID, Any>() }

        if (ordered.isEmpty()) {
            return values
        }

        val fields = ordered.map { it.key.toString() }.toTypedArray()

        // Every key lives in the same hash, so one HMGET per profile reads them all at once,
        // whatever their types.
        pipelined(uuids, { pipeline, uuid -> pipeline.hmget(profileKey(uuid), *fields) }) { uuid, stored ->
            stored.forEachIndexed { index, value ->
                val key = ordered[index]

                @Suppress("UNCHECKED_CAST")
                val serializer = key.type.getSerializer(this) as RedisSerializer<Any>
                serializer.decodeOrNull(value, fields[index])?.let { values.getValue(key)[uuid] = it }
            }
        }

        return values
    }

    /**
     * Run one command per uuid in pipelined chunks, so a read of the whole playerbase is a handful
     * of round trips without buffering every reply at once.
     */
    private fun <R> pipelined(
        uuids: Set<UUID>,
        command: (redis.clients.jedis.Pipeline, UUID) -> Response<R>,
        consume: (UUID, R) -> Unit
    ) {
        if (uuids.isEmpty()) {
            return
        }

        withConnection { jedis ->
            for (chunk in uuids.chunked(PIPELINE_CHUNK)) {
                val pipeline = jedis.pipelined()
                val responses = chunk.map { it to command(pipeline, it) }
                pipeline.sync()

                for ((uuid, response) in responses) {
                    consume(uuid, response.get() ?: continue)
                }
            }
        }
    }

    private fun <T : Any> registerSerializer(
        type: PersistentDataKeyType<T>,
        encoder: (T) -> String?,
        decoder: (String) -> T
    ) {
        type.registerSerializer(this, object : RedisSerializer<T>() {
            override fun encode(value: T) = encoder(value)
            override fun decode(stored: String) = decoder(stored)
        })
    }

    private abstract inner class RedisSerializer<T : Any> : DataTypeSerializer<T>() {
        /**
         * The stored form of [value], or null to store no value at all.
         */
        abstract fun encode(value: T): String?

        abstract fun decode(stored: String): T

        /**
         * Decode a value read in bulk, reporting one that cannot be decoded as absent, as the base
         * readAll reports a failed read.
         */
        fun decodeOrNull(stored: String?, field: String): T? {
            if (stored == null) {
                return null
            }

            return try {
                decode(stored)
            } catch (e: Exception) {
                logger.log(Level.WARNING, "Could not decode stored Redis value for $field", e)
                null
            }
        }

        override fun readAsync(uuid: UUID, key: PersistentDataKey<T>): T? {
            val stored = withConnection { it.hget(profileKey(uuid), key.key.toString()) } ?: return null
            return decode(stored)
        }

        override fun writeAsync(uuid: UUID, key: PersistentDataKey<T>, value: T) {
            val field = key.key.toString()
            val encoded = encode(value)
            val sender = syncSender

            withConnection { jedis ->
                val transaction = jedis.multi()

                if (encoded == null) {
                    transaction.hdel(profileKey(uuid), field)
                } else {
                    transaction.hset(profileKey(uuid), field, encoded)
                    transaction.sadd(indexKey, uuid.toString())
                }

                if (sender != null) {
                    transaction.publish(syncChannel, syncMessage(sender, uuid, field))
                }

                transaction.exec()
            }
        }
    }

    companion object {
        private const val PIPELINE_CHUNK = 1000

        /**
         * Connect from the `redis` section of config.yml.
         *
         * A blank `sentinel.master` connects to `host`:`port` directly; otherwise the master is
         * found through the listed sentinels, and followed across a failover.
         */
        fun fromConfig(config: Config): RedisPersistentDataHandler {
            val connections = config.getIntOrNull("connections") ?: 10
            val sync = config.getBoolOrNull("sync") ?: false

            val poolConfig = GenericObjectPoolConfig<Jedis>().apply {
                // The sync subscriber holds one connection for as long as it runs, so it gets its
                // own rather than taking one a read on the calling thread needs.
                maxTotal = connections + if (sync) 1 else 0
                maxIdle = maxTotal
                testWhileIdle = true
            }

            val clientConfig = DefaultJedisClientConfig.builder()
                .user(config.getStringOrNull("user")?.ifBlank { null })
                .password(config.getStringOrNull("password")?.ifBlank { null })
                .database(config.getIntOrNull("database") ?: 0)
                .ssl(config.getBoolOrNull("ssl") ?: false)
                .build()

            val master = config.getStringOrNull("sentinel.master")?.ifBlank { null }

            val pool: Pool<Jedis> = if (master != null) {
                val sentinels = config.getStrings("sentinel.nodes").map { HostAndPort.from(it) }.toSet()

                require(sentinels.isNotEmpty()) { "redis.sentinel.master is set, but redis.sentinel.nodes is empty" }

                val sentinelConfig = DefaultJedisClientConfig.builder()
                    .password(config.getStringOrNull("sentinel.password")?.ifBlank { null })
                    .ssl(config.getBoolOrNull("ssl") ?: false)
                    .build()

                JedisSentinelPool(master, sentinels, poolConfig, clientConfig, sentinelConfig)
            } else {
                JedisPool(
                    poolConfig,
                    HostAndPort(config.getStringOrNull("host") ?: "localhost", config.getIntOrNull("port") ?: 6379),
                    clientConfig
                )
            }

            // One below the pool size, as for MySQL, so a read on the calling thread always has a
            // connection to take.
            return RedisPersistentDataHandler(pool, config.getStringOrNull("prefix") ?: "eco:", connections - 1)
        }

        fun syncMessage(sender: String, uuid: UUID, field: String) = "$sender|$uuid|$field"
    }
}
