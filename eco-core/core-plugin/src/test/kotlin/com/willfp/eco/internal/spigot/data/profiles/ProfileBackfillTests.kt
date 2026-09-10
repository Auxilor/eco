package com.willfp.eco.internal.spigot.data.profiles

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
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * The migration only carries the keys that exist when it runs, so data.yml keeps data for keys
 * registered later. The backfill is what eventually carries those, and it runs on a live server -
 * so it must never overwrite a value the server has since stored.
 */
class ProfileBackfillTests {
    companion object {
        @JvmStatic
        @BeforeAll
        fun stubEco() = stubEcoConfigFactory()

        @JvmStatic
        @AfterAll
        fun unstubEco() = unstubEcoConfigFactory()
    }

    private val lateKey =
        PersistentDataKey(namespacedKeyOf("backfill", "late"), PersistentDataKeyType.STRING, "")
    private val carriedKey =
        PersistentDataKey(namespacedKeyOf("backfill", "carried"), PersistentDataKeyType.INT, 0)
    private val unstoredKey =
        PersistentDataKey(namespacedKeyOf("backfill", "unstored"), PersistentDataKeyType.STRING, "")

    private val alice = UUID.randomUUID()
    private val bob = UUID.randomUUID()

    private fun tempDatabase(): File =
        Files.createTempDirectory("eco-backfill-test").resolve("data.db").toFile()

    private fun source() = YamlPersistentDataHandler(
        configOf(
            "player" to mapOf(
                alice.toString() to mapOf(
                    "backfill:late" to "from data.yml",
                    "backfill:carried" to 1
                ),
                bob.toString() to mapOf(
                    "backfill:late" to "bob from data.yml"
                )
            )
        )
    )

    @Test
    fun `a key the migration never carried is backfilled`() {
        val file = tempDatabase()
        val target = SQLitePersistentDataHandler(file)

        val written = backfillProfiles(source(), target, setOf(lateKey)) { }

        target.shutdown()

        assertEquals(2, written)

        val reader = SQLitePersistentDataHandler(file)
        try {
            assertEquals("from data.yml", reader.read(alice, lateKey))
            assertEquals("bob from data.yml", reader.read(bob, lateKey))
        } finally {
            reader.shutdown()
        }
    }

    @Test
    fun `a profile that already has a value keeps it`() {
        val file = tempDatabase()
        val target = SQLitePersistentDataHandler(file)
        target.write(alice, carriedKey, 99)
        target.shutdown()

        val second = SQLitePersistentDataHandler(file)
        val written = backfillProfiles(source(), second, setOf(carriedKey)) { }
        second.shutdown()

        assertEquals(0, written)

        val reader = SQLitePersistentDataHandler(file)
        try {
            assertEquals(99, reader.read(alice, carriedKey))
        } finally {
            reader.shutdown()
        }
    }

    @Test
    fun `a key with nothing stored in the source writes nothing`() {
        val file = tempDatabase()
        val target = SQLitePersistentDataHandler(file)

        val written = backfillProfiles(source(), target, setOf(unstoredKey)) { }

        target.shutdown()

        assertEquals(0, written)

        val reader = SQLitePersistentDataHandler(file)
        try {
            assertEquals(null, reader.read(alice, unstoredKey))
        } finally {
            reader.shutdown()
        }
    }
}
