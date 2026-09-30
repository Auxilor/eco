package com.willfp.eco.internal.spigot.data.profiles

import com.github.fppt.jedismock.RedisServer
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.data.handlers.impl.RedisPersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.stubEcoConfigFactory
import com.willfp.eco.internal.spigot.data.handlers.unstubEcoConfigFactory
import com.willfp.eco.util.namespacedKeyOf
import java.util.Collections
import java.util.UUID
import java.util.logging.Logger
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool

/**
 * Two servers sharing one Redis: a write on one has to reach the other's cache, and nothing else
 * may - not its own writes, not a key it has a newer value for, not a key it has never heard of.
 */
class RedisProfileSyncTests {
    companion object {
        @JvmStatic
        @BeforeAll
        fun stubEco() = stubEcoConfigFactory()

        @JvmStatic
        @AfterAll
        fun unstubEco() = unstubEcoConfigFactory()

        private const val TIMEOUT_MS = 5_000L
    }

    private val key = PersistentDataKey(namespacedKeyOf("sync", "int"), PersistentDataKeyType.INT, 0)
    private val otherKey = PersistentDataKey(namespacedKeyOf("sync", "other"), PersistentDataKeyType.INT, 0)

    private class RecordingProfiles : SyncedProfiles {
        val invalidated: MutableList<Pair<UUID, PersistentDataKey<*>>> = Collections.synchronizedList(mutableListOf())
        val changed: MutableList<Triple<UUID, PersistentDataKey<*>, Any>> = Collections.synchronizedList(mutableListOf())

        @Volatile
        var resyncs = 0

        @Volatile
        var pending: Pair<UUID, PersistentDataKey<*>>? = null

        override fun invalidate(uuid: UUID, key: PersistentDataKey<*>) {
            invalidated += uuid to key
        }

        override fun invalidateAllShared() {
            resyncs++
        }

        override fun isPending(uuid: UUID, key: PersistentDataKey<*>) = pending == (uuid to key)

        override fun onRemoteWrite(uuid: UUID, key: PersistentDataKey<*>, value: Any) {
            changed += Triple(uuid, key, value)
        }
    }

    private lateinit var server: RedisServer
    private val handlers = mutableListOf<RedisPersistentDataHandler>()
    private val syncs = mutableListOf<RedisProfileSync>()

    @BeforeEach
    fun startServer() {
        server = RedisServer.newRedisServer().start()
    }

    @AfterEach
    fun stopAll() {
        syncs.forEach { it.stop() }
        handlers.forEach { it.shutdown() }
        server.stop()
    }

    private fun handler() =
        RedisPersistentDataHandler(JedisPool(server.host, server.bindPort), "eco:", 4).also { handlers += it }

    private fun sync(
        handler: RedisPersistentDataHandler,
        id: String,
        profiles: SyncedProfiles
    ) = RedisProfileSync(handler, id, profiles, Logger.getLogger("test"), initialBackoffMillis = 50)
        .also { syncs += it }
        .also { it.start() }
        .also { started -> waitUntil { started.isSubscribed } }

    private fun waitUntil(condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + TIMEOUT_MS
        while (!condition()) {
            check(System.currentTimeMillis() < deadline) { "timed out" }
            Thread.sleep(10)
        }
    }

    /** Write through a handler and wait until the write, and its announcement, are in Redis. */
    private fun writeAndFlush(handler: RedisPersistentDataHandler, uuid: UUID, key: PersistentDataKey<Int>, value: Int) {
        handler.write(uuid, key, value)
        waitUntil { handler.read(uuid, key) == value }
    }

    @Test
    fun `a write on one server invalidates and reports it on the other`() {
        val a = handler()
        val b = RecordingProfiles()
        sync(a, "a", RecordingProfiles())
        sync(handler(), "b", b)
        val uuid = UUID.randomUUID()

        writeAndFlush(a, uuid, key, 7)
        waitUntil { b.changed.isNotEmpty() }

        assertEquals(listOf(uuid to key), b.invalidated.toList())
        assertEquals(listOf(Triple<UUID, PersistentDataKey<*>, Any>(uuid, key, 7)), b.changed.toList())
    }

    @Test
    fun `a server ignores its own writes`() {
        val own = RecordingProfiles()
        val other = RecordingProfiles()
        val a = handler()
        sync(a, "a", own)
        sync(handler(), "b", other)
        val uuid = UUID.randomUUID()

        writeAndFlush(a, uuid, key, 1)
        waitUntil { other.changed.isNotEmpty() }

        assertTrue(own.invalidated.isEmpty())
        assertTrue(own.changed.isEmpty())
    }

    @Test
    fun `a newer pending value is not invalidated`() {
        val a = handler()
        val b = RecordingProfiles()
        sync(a, "a", RecordingProfiles())
        sync(handler(), "b", b)
        val uuid = UUID.randomUUID()
        b.pending = uuid to key

        writeAndFlush(a, uuid, key, 1)
        // A second write, to a key with nothing pending, marks when the first has been handled.
        writeAndFlush(a, uuid, otherKey, 2)
        waitUntil { b.changed.isNotEmpty() }

        assertEquals(listOf(uuid to otherKey), b.invalidated.toList())
    }

    @Test
    fun `a key this server has never registered is ignored`() {
        val a = handler()
        val b = RecordingProfiles()
        sync(a, "a", RecordingProfiles())
        sync(handler(), "b", b)
        val uuid = UUID.randomUUID()

        Jedis(server.host, server.bindPort).use {
            it.publish(a.syncChannel, RedisPersistentDataHandler.syncMessage("a", uuid, "sync:never_registered"))
            it.publish(a.syncChannel, "garbage")
        }
        writeAndFlush(a, uuid, key, 3)
        waitUntil { b.changed.isNotEmpty() }

        assertEquals(listOf(uuid to key), b.invalidated.toList())
    }

    @Test
    fun `writes are only announced while sync runs`() {
        val handler = handler()
        assertNull(handler.syncSender)

        val sync = sync(handler, "a", RecordingProfiles())
        assertEquals("a", handler.syncSender)

        sync.stop()
        assertNull(handler.syncSender)
        assertFalse(sync.isSubscribed)
    }

    @Test
    fun `resubscribing after a dropped connection drops every shared cached value`() {
        val profiles = RecordingProfiles()
        val port = server.bindPort
        sync(handler(), "b", profiles)
        assertEquals(0, profiles.resyncs)

        server.stop()
        waitUntil { !syncs.single().isSubscribed }
        server = RedisServer.newRedisServer(port).start()

        waitUntil { syncs.single().isSubscribed }
        assertEquals(1, profiles.resyncs)
    }
}
