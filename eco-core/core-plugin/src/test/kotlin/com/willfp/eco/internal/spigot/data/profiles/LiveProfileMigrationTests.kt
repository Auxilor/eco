package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.core.data.handlers.DataTypeSerializer
import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.data.handlers.configOf
import com.willfp.eco.internal.spigot.data.handlers.impl.SQLitePersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.impl.YamlPersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.stubEcoConfigFactory
import com.willfp.eco.internal.spigot.data.handlers.unstubEcoConfigFactory
import com.willfp.eco.util.namespacedKeyOf
import java.io.File
import java.nio.file.Files
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.TimeUnit
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * The live migration runs against a server that is up: players are reading and writing the same
 * profiles it is carrying, so what it must never do is put an older value over a newer one.
 */
class LiveProfileMigrationTests {
    companion object {
        @JvmStatic
        @BeforeAll
        fun stubEco() = stubEcoConfigFactory()

        @JvmStatic
        @AfterAll
        fun unstubEco() = unstubEcoConfigFactory()
    }

    private val firstKey =
        PersistentDataKey(namespacedKeyOf("live", "first"), PersistentDataKeyType.STRING, "")
    private val secondKey =
        PersistentDataKey(namespacedKeyOf("live", "second"), PersistentDataKeyType.STRING, "")

    private val alice = UUID.randomUUID()
    private val bob = UUID.randomUUID()

    // Ordered so that a test can rely on which key is carried first.
    private val keys = linkedSetOf<PersistentDataKey<*>>(firstKey, secondKey)

    private fun tempDatabase(): File =
        Files.createTempDirectory("eco-live-test").resolve("data.db").toFile()

    private fun source() = YamlPersistentDataHandler(
        configOf(
            "player" to mapOf(
                alice.toString() to mapOf(
                    "live:first" to "alice first",
                    "live:second" to "alice second"
                ),
                bob.toString() to mapOf(
                    "live:first" to "bob first"
                )
            )
        )
    )

    private fun migrationInto(
        target: PersistentDataHandler,
        source: PersistentDataHandler = source(),
        targetFor: (PersistentDataKey<*>) -> PersistentDataHandler = { target }
    ) = LiveProfileMigration(source, { keys }, targetFor) { }

    @Test
    fun `a profile the target holds nothing for is carried across`() {
        val file = tempDatabase()
        val target = SQLitePersistentDataHandler(file)

        migrationInto(target).ensureMigrated(alice)

        target.shutdown()

        val reader = SQLitePersistentDataHandler(file)
        try {
            assertEquals("alice first", reader.read(alice, firstKey))
            assertEquals("alice second", reader.read(alice, secondKey))
        } finally {
            reader.shutdown()
        }
    }

    @Test
    fun `a profile the target already holds is left alone`() {
        val file = tempDatabase()
        val existing = SQLitePersistentDataHandler(file)
        existing.write(alice, firstKey, "already here")
        existing.shutdown()

        val target = SQLitePersistentDataHandler(file)
        migrationInto(target).ensureMigrated(alice)
        target.shutdown()

        val reader = SQLitePersistentDataHandler(file)
        try {
            assertEquals("already here", reader.read(alice, firstKey))
            // The rest of the profile is not carried either: the target holding a row for it means
            // it migrated already, and everything since is the server's own writing.
            assertNull(reader.read(alice, secondKey))
        } finally {
            reader.shutdown()
        }
    }

    @Test
    fun `a key written while the profile is being copied is not overwritten`() {
        val file = tempDatabase()
        val target = SQLitePersistentDataHandler(file)

        lateinit var migration: LiveProfileMigration

        // Stands in for the server writing to the profile mid-copy: while the first key is being
        // read out of the source, the writer commits the second one to both stores.
        val source = ScriptedSource(setOf(alice)) { key ->
            if (key == firstKey) {
                target.write(alice, secondKey, "written live")
                migration.noteLiveWrite(alice, secondKey)
            }

            "alice ${key.key.key}"
        }

        migration = LiveProfileMigration(source, { keys }, { target }) { }
        migration.ensureMigrated(alice)

        target.shutdown()

        val reader = SQLitePersistentDataHandler(file)
        try {
            assertEquals("alice first", reader.read(alice, firstKey))
            assertEquals("written live", reader.read(alice, secondKey))
        } finally {
            reader.shutdown()
        }
    }

    @Test
    fun `the sweep carries every profile and then reports itself complete`() {
        val file = tempDatabase()
        val target = SQLitePersistentDataHandler(file)

        val migration = migrationInto(target)
        assertEquals(false, migration.isComplete)

        migration.sweep()

        assertTrue(migration.isComplete)

        target.shutdown()

        val reader = SQLitePersistentDataHandler(file)
        try {
            assertEquals("alice first", reader.read(alice, firstKey))
            assertEquals("bob first", reader.read(bob, firstKey))
        } finally {
            reader.shutdown()
        }
    }

    /**
     * A source whose every answer is [answer], so a test can hook the moment a key is read.
     */
    private class ScriptedSource(
        private val uuids: Set<UUID>,
        answer: (PersistentDataKey<String>) -> String
    ) : PersistentDataHandler("scripted") {
        init {
            PersistentDataKeyType.STRING.registerSerializer(this, object : DataTypeSerializer<String>() {
                override fun readAsync(uuid: UUID, key: PersistentDataKey<String>) = answer(key)

                override fun writeAsync(uuid: UUID, key: PersistentDataKey<String>, value: String) =
                    throw UnsupportedOperationException()
            })
        }

        override fun getSavedUUIDs() = uuids
    }

    /**
     * A source that answers with the same value every time and counts what was asked of it, so a
     * test can tell one copy from two.
     */
    private class CountingSource(private val uuids: Set<UUID>) : PersistentDataHandler("counting") {
        val reads = AtomicInteger()

        init {
            PersistentDataKeyType.STRING.registerSerializer(this, object : DataTypeSerializer<String>() {
                override fun readAsync(uuid: UUID, key: PersistentDataKey<String>): String {
                    reads.incrementAndGet()

                    // Slow enough that a second thread is inside ensureMigrated while the first is
                    // still carrying keys, which is the case the per-profile lock exists for.
                    Thread.sleep(50)
                    return "from the source"
                }

                override fun writeAsync(uuid: UUID, key: PersistentDataKey<String>, value: String) =
                    throw UnsupportedOperationException()
            })
        }

        override fun getSavedUUIDs() = uuids
    }

    @Test
    fun `two threads asking for the same profile copy it once`() {
        val file = tempDatabase()
        val target = SQLitePersistentDataHandler(file)

        val source = CountingSource(setOf(alice))
        val migration = LiveProfileMigration(source, { keys }, { target }) { }

        val start = CountDownLatch(1)
        val done = CountDownLatch(2)

        repeat(2) {
            Thread {
                start.await()
                migration.ensureMigrated(alice)
                done.countDown()
            }.start()
        }

        start.countDown()
        assertTrue(done.await(30, TimeUnit.SECONDS))

        target.shutdown()

        // One read per key, rather than one per key per thread.
        assertEquals(keys.size, source.reads.get())
    }
}
