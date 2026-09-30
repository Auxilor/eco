package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.core.config.interfaces.Config
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

    private fun source() = YamlPersistentDataHandler(sourceConfig())

    private fun sourceConfig() = (
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
    ) = LiveProfileMigration(source, { keys }, targetFor, { })

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
                migration.noteWrite(alice, secondKey)
            }

            "alice ${key.key.key}"
        }

        migration = LiveProfileMigration(source, { keys }, { target }, { })
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
        val migration = LiveProfileMigration(source, { keys }, { target }, { })

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

    @Test
    fun `the sweep pauses once for every profile it carries`() {
        val file = tempDatabase()
        val target = SQLitePersistentDataHandler(file)

        val pauses = AtomicInteger()

        LiveProfileMigration(source(), { keys }, { target }, { }) { pauses.incrementAndGet() }
            .sweep()

        target.shutdown()

        // One for Alice, one for Bob.
        assertEquals(2, pauses.get())
    }

    @Test
    fun `a profile already carried across costs the sweep no pause`() {
        val file = tempDatabase()
        val target = SQLitePersistentDataHandler(file)

        val pauses = AtomicInteger()

        val migration =
            LiveProfileMigration(source(), { keys }, { target }, { }) { pauses.incrementAndGet() }

        // A login reaching Alice before the sweep does.
        migration.ensureMigrated(alice)

        migration.sweep()

        target.shutdown()

        // Only Bob is left to carry, so the throttle is paid once rather than twice.
        assertEquals(1, pauses.get())
    }

    @Test
    fun `a profile the target already holds costs the sweep no pause`() {
        val file = tempDatabase()

        // A migration that carried Alice across and then died, as a restart mid-sweep leaves it.
        // Shut down rather than left running: writes are dispatched to the handler's executor, so
        // this is what makes Alice readable by the sweep below rather than a race with it.
        val seed = SQLitePersistentDataHandler(file)
        LiveProfileMigration(source(), { keys }, { seed }, { }).ensureMigrated(alice)
        seed.shutdown()

        val target = SQLitePersistentDataHandler(file)
        val pauses = AtomicInteger()

        LiveProfileMigration(source(), { keys }, { target }, { }) { pauses.incrementAndGet() }
            .sweep()

        target.shutdown()

        // The resumed sweep only copies Bob: Alice costs a lookup, and a lookup is not worth
        // throttling, or a server restarting near the end of a sweep would sleep through it again.
        assertEquals(1, pauses.get())
    }

    @Test
    fun `a login is never held up by the throttle`() {
        val file = tempDatabase()
        val target = SQLitePersistentDataHandler(file)

        val pauses = AtomicInteger()

        LiveProfileMigration(source(), { keys }, { target }, { }) { pauses.incrementAndGet() }
            .ensureMigrated(alice)

        target.shutdown()

        // The throttle exists to slow the background sweep, not the player waiting to join.
        assertEquals(0, pauses.get())
    }

    @Test
    fun `verification carries across a key a half-finished copy left behind`() {
        val file = tempDatabase()

        // A profile a previous boot died part-way through copying: the target holds the first key
        // and nothing else, which is enough for the sweep to treat it as already carried. Written
        // through a handler that is then shut down, which is what awaits the write.
        val seed = SQLitePersistentDataHandler(file)
        seed.write(alice, firstKey, "alice first")
        seed.shutdown()

        val target = SQLitePersistentDataHandler(file)

        val migration = LiveProfileMigration(source(), { keys }, { target }, { })

        migration.sweep()

        assertNull(target.read(alice, secondKey))

        assertEquals(1, migration.verify())

        assertEquals("alice second", target.read(alice, secondKey))

        target.shutdown()
    }

    @Test
    fun `verification leaves a profile the server wrote to alone`() {
        val file = tempDatabase()

        val seed = SQLitePersistentDataHandler(file)
        seed.write(alice, firstKey, "alice first")
        seed.shutdown()

        val target = SQLitePersistentDataHandler(file)

        val migration = LiveProfileMigration(source(), { keys }, { target }, { })

        migration.sweep()

        // The server wrote to Alice while the migration ran, so what the target holds for her is
        // hers now: a list she emptied looks exactly like a key the copy never reached.
        migration.noteWrite(alice, firstKey)

        assertEquals(0, migration.verify())

        assertNull(target.read(alice, secondKey))

        target.shutdown()
    }

    @Test
    fun `verification only reads the profiles the sweep skipped`() {
        val file = tempDatabase()

        // Alice is a profile the sweep skips, Bob one it carries. Written through a handler that
        // is then shut down, which is what awaits the write.
        val seed = SQLitePersistentDataHandler(file)
        seed.write(alice, firstKey, "alice first")
        seed.shutdown()

        val target = SQLitePersistentDataHandler(file)
        val reads = mutableListOf<String>()
        val migration = LiveProfileMigration(
            YamlPersistentDataHandler(RecordingConfig(sourceConfig(), reads)),
            { keys },
            { target },
            { }
        )

        migration.sweep()

        reads.clear()

        migration.verify()

        // A profile this sweep carried was carried whole, so re-reading it is a pass over the
        // entire playerbase, times every key on the server, to find nothing.
        assertTrue(reads.none { it.contains(bob.toString()) }, "Bob was read back: $reads")
        assertTrue(reads.any { it.contains(alice.toString()) }, "Alice was not verified")

        target.shutdown()
    }

    @Test
    fun `verification finds nothing to do after a clean sweep`() {
        val file = tempDatabase()
        val target = SQLitePersistentDataHandler(file)

        val migration = LiveProfileMigration(source(), { keys }, { target }, { })

        migration.sweep()

        assertEquals(0, migration.verify())

        target.shutdown()
    }

    @Test
    fun `verification reports its progress as it goes`() {
        val file = tempDatabase()

        val seed = SQLitePersistentDataHandler(file)
        seed.write(alice, firstKey, "alice first")
        seed.shutdown()

        val target = SQLitePersistentDataHandler(file)

        val lines = mutableListOf<String>()
        val migration = LiveProfileMigration(source(), { keys }, { target }, { lines += it })

        migration.sweep()

        lines.clear()

        migration.verify()

        target.shutdown()

        // Two keys, so the halfway mark is the only milestone crossed before the end. The pass
        // is the longest part of a migration on a server with thousands of keys, and a console
        // that says nothing for a quarter of an hour looks like a hang.
        assertEquals(
            listOf(
                "Checking 1 profiles the sweep skipped against 2 keys",
                "Checked 50% of keys (1/2)",
                "Finished checking the profiles the sweep skipped"
            ),
            lines
        )
    }

    @Test
    fun `verification with nothing to check says nothing`() {
        val file = tempDatabase()
        val target = SQLitePersistentDataHandler(file)

        val lines = mutableListOf<String>()
        val migration = LiveProfileMigration(source(), { keys }, { target }, { lines += it })

        migration.sweep()

        lines.clear()

        migration.verify()

        target.shutdown()

        // Nothing was skipped, so there is nothing to check and nothing to report.
        assertEquals(emptyList<String>(), lines)
    }

    @Test
    fun `the sweep reports its progress as it goes`() {
        val file = tempDatabase()
        val target = SQLitePersistentDataHandler(file)

        val lines = mutableListOf<String>()

        LiveProfileMigration(source(), { keys }, { target }, { lines += it }).sweep()

        target.shutdown()

        // Two profiles, so the halfway mark is the only milestone crossed before the end.
        assertEquals(
            listOf("Migrated 50% of profiles (1/2)"),
            lines.filter { it.startsWith("Migrated ") }
        )
    }

    @Test
    fun `the last profile is left to the finishing line rather than reported as progress`() {
        val file = tempDatabase()
        val target = SQLitePersistentDataHandler(file)

        val lines = mutableListOf<String>()

        val single = YamlPersistentDataHandler(
            configOf("player" to mapOf(alice.toString() to mapOf("live:first" to "alice first")))
        )

        LiveProfileMigration(single, { keys }, { target }, { lines += it }).sweep()

        target.shutdown()

        // A one profile migration is 100% done the moment it starts working, and saying so twice
        // over is noise: the finishing line covers it.
        assertTrue(lines.none { it.startsWith("Migrated ") })
    }
}

/**
 * A config that remembers the paths read through it, so a test can see what was looked at.
 */
private class RecordingConfig(
    private val delegate: Config,
    private val reads: MutableList<String>
) : Config by delegate {
    override fun getStringOrNull(path: String): String? {
        reads.add(path)

        return delegate.getStringOrNull(path)
    }
}
