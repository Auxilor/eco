package com.willfp.eco.internal.spigot.data.handlers

import com.github.fppt.jedismock.RedisServer
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.config.EcoConfigSection
import com.willfp.eco.internal.spigot.data.handlers.impl.RedisPersistentDataHandler
import com.willfp.eco.util.namespacedKeyOf
import java.math.BigDecimal
import java.util.UUID
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool

/**
 * Redis is a shared store for a whole network, so each key type's round trip is pinned against a
 * real RESP server (jedis-mock) rather than a mocked client: the encoding is what every server on
 * the network has to agree on.
 */
class RedisPersistentDataHandlerTests {
    companion object {
        @JvmStatic
        @BeforeAll
        fun stubEco() = stubEcoConfigFactory()

        @JvmStatic
        @AfterAll
        fun unstubEco() = unstubEcoConfigFactory()
    }

    private val stringKey =
        PersistentDataKey(namespacedKeyOf("redis", "string"), PersistentDataKeyType.STRING, "")
    private val boolKey =
        PersistentDataKey(namespacedKeyOf("redis", "bool"), PersistentDataKeyType.BOOLEAN, false)
    private val intKey =
        PersistentDataKey(namespacedKeyOf("redis", "int"), PersistentDataKeyType.INT, 0)
    private val doubleKey =
        PersistentDataKey(namespacedKeyOf("redis", "double"), PersistentDataKeyType.DOUBLE, 0.0)
    private val decimalKey = PersistentDataKey(
        namespacedKeyOf("redis", "decimal"),
        PersistentDataKeyType.BIG_DECIMAL,
        BigDecimal.ZERO
    )
    private val configKey = PersistentDataKey(
        namespacedKeyOf("redis", "config"),
        PersistentDataKeyType.CONFIG,
        EcoConfigSection(ConfigType.JSON) as Config
    )
    private val listKey = PersistentDataKey(
        namespacedKeyOf("redis", "list"),
        PersistentDataKeyType.STRING_LIST,
        emptyList<String>()
    )

    private lateinit var server: RedisServer

    @BeforeEach
    fun startServer() {
        server = RedisServer.newRedisServer().start()
    }

    @AfterEach
    fun stopServer() {
        server.stop()
    }

    private fun handler(prefix: String = "eco:") =
        RedisPersistentDataHandler(JedisPool(server.host, server.bindPort), prefix, 4)

    private fun raw(): Jedis = Jedis(server.host, server.bindPort)

    private fun <T> writingThenReading(
        write: (RedisPersistentDataHandler) -> Unit,
        read: (RedisPersistentDataHandler) -> T
    ): T {
        val writer = handler()
        write(writer)
        writer.shutdown()

        val reader = handler()
        try {
            return read(reader)
        } finally {
            reader.shutdown()
        }
    }

    @Test
    fun `every simple key type round trips`() {
        val uuid = UUID.randomUUID()

        val values = writingThenReading(
            {
                it.write(uuid, stringKey, "héllo ✓")
                it.write(uuid, boolKey, true)
                it.write(uuid, intKey, -42)
                it.write(uuid, doubleKey, 1.5)
                it.write(uuid, listKey, listOf("a", "b, c", "\"d\""))
            },
            {
                listOf(
                    it.read(uuid, stringKey),
                    it.read(uuid, boolKey),
                    it.read(uuid, intKey),
                    it.read(uuid, doubleKey),
                    it.read(uuid, listKey)
                )
            }
        )

        assertEquals(listOf<Any?>("héllo ✓", true, -42, 1.5, listOf("a", "b, c", "\"d\"")), values)
    }

    @Test
    fun `non-finite doubles round trip`() {
        val uuid = UUID.randomUUID()
        val other = UUID.randomUUID()

        val values = writingThenReading(
            {
                it.write(uuid, doubleKey, Double.NaN)
                it.write(other, doubleKey, Double.NEGATIVE_INFINITY)
            },
            { listOf(it.read(uuid, doubleKey), it.read(other, doubleKey)) }
        )

        assertEquals(listOf(Double.NaN, Double.NEGATIVE_INFINITY), values)
    }

    @Test
    fun `a nested config value round trips`() {
        val uuid = UUID.randomUUID()

        val stored = writingThenReading(
            { it.write(uuid, configKey, configOf("nested" to mapOf("value" to 3, "name" to "x"))) },
            { it.read(uuid, configKey) }
        )

        assertEquals(3, stored?.getInt("nested.value"))
        assertEquals("x", stored?.getString("nested.name"))
    }

    @Test
    fun `a big decimal keeps its scale and precision`() {
        val uuid = UUID.randomUUID()
        val other = UUID.randomUUID()
        val long = BigDecimal("123456789012345678901234567890.1234")

        val values = writingThenReading(
            {
                it.write(uuid, decimalKey, BigDecimal("100.50"))
                it.write(other, decimalKey, long)
            },
            { listOf(it.read(uuid, decimalKey), it.read(other, decimalKey)) }
        )

        assertEquals("100.50", values[0]?.toPlainString())
        assertEquals(long, values[1])
    }

    @Test
    fun `a missing value reads as null`() {
        val stored = writingThenReading({}, { it.read(UUID.randomUUID(), intKey) })

        assertNull(stored)
    }

    @Test
    fun `an empty list removes the stored field`() {
        val uuid = UUID.randomUUID()

        // Separate sessions: writes run in parallel on the executor, so two in one session to the
        // same key could land in either order.
        val seed = handler()
        seed.write(uuid, listKey, listOf("a"))
        seed.shutdown()

        val values = writingThenReading(
            { it.write(uuid, listKey, emptyList()) },
            { it.readAll(setOf(uuid), listKey) }
        )

        assertEquals(emptyMap<UUID, List<String>>(), values)
        raw().use { assertEquals(false, it.hexists("eco:profile:$uuid", listKey.key.toString())) }
    }

    @Test
    fun `readAll omits uuids with no stored value`() {
        val stored = UUID.randomUUID()
        val absent = UUID.randomUUID()

        val values = writingThenReading(
            { it.write(stored, intKey, 7) },
            { it.readAll(setOf(stored, absent), intKey) }
        )

        assertEquals(mapOf(stored to 7), values)
    }

    @Test
    fun `readAll of no uuids is empty`() {
        val values = writingThenReading({}, { it.readAll(emptySet(), intKey) })

        assertEquals(emptyMap<UUID, Int>(), values)
    }

    @Test
    fun `readAllKeys reports every requested key`() {
        val first = UUID.randomUUID()
        val second = UUID.randomUUID()

        val values = writingThenReading(
            {
                it.write(first, intKey, 1)
                it.write(second, intKey, 2)
                it.write(second, stringKey, "two")
            },
            { it.readAllKeys(setOf(first, second), listOf(intKey, stringKey, boolKey)) }
        )

        assertEquals(mapOf(first to 1, second to 2), values[intKey])
        assertEquals(mapOf(second to "two"), values[stringKey])
        assertEquals(emptyMap<UUID, Any>(), values[boolKey])
    }

    @Test
    fun `saved uuids come from the index and never decode values`() {
        val first = UUID.randomUUID()
        val second = UUID.randomUUID()

        val uuids = writingThenReading(
            {
                it.write(first, intKey, 1)
                it.write(second, stringKey, "x")
                it.shutdown()
                // A value no serializer can decode must not break the leaderboard's scan.
                raw().use { jedis -> jedis.hset("eco:profile:$first", intKey.key.toString(), "not a number") }
            },
            { it.getSavedUUIDs() }
        )

        assertEquals(setOf(first, second), uuids)
    }

    @Test
    fun `a profile exists once anything is written to it`() {
        val stored = UUID.randomUUID()
        val absent = UUID.randomUUID()

        val exists = writingThenReading(
            { it.write(stored, boolKey, false) },
            { listOf(it.hasStoredProfile(stored), it.hasStoredProfile(absent)) }
        )

        assertEquals(listOf(true, false), exists)
    }

    @Test
    fun `a value that fails to decode reads as null`() {
        val uuid = UUID.randomUUID()
        raw().use { it.hset("eco:profile:$uuid", intKey.key.toString(), "not a number") }

        val stored = writingThenReading({}, { it.read(uuid, intKey) })

        assertNull(stored)
    }

    @Test
    fun `handlers with different prefixes do not see each other`() {
        val uuid = UUID.randomUUID()

        val a = handler("a:")
        a.write(uuid, intKey, 1)
        a.shutdown()

        val b = handler("b:")
        try {
            assertNull(b.read(uuid, intKey))
            assertEquals(emptySet<UUID>(), b.getSavedUUIDs())
        } finally {
            b.shutdown()
        }
    }

    @Test
    fun `every write queued before shutdown is stored`() {
        val uuids = List(500) { UUID.randomUUID() }

        val values = writingThenReading(
            { handler -> uuids.forEachIndexed { index, uuid -> handler.write(uuid, intKey, index) } },
            { it.readAll(uuids.toSet(), intKey) }
        )

        assertEquals(uuids.size, values.size)
        assertEquals(uuids.indices.toList(), uuids.map { values[it] })
    }
}
