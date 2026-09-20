package com.willfp.eco.internal.spigot.data.handlers

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.config.EcoConfigSection
import com.willfp.eco.internal.spigot.data.handlers.impl.SQLitePersistentDataHandler
import com.willfp.eco.util.namespacedKeyOf
import java.io.File
import java.math.BigDecimal
import java.nio.file.Files
import java.util.UUID
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * SQLite is the default store for every single-node server, so each key type's round trip is
 * pinned against a real database file rather than a mock.
 */
class SQLitePersistentDataHandlerTests {
    companion object {
        @JvmStatic
        @BeforeAll
        fun stubEco() = stubEcoConfigFactory()

        @JvmStatic
        @AfterAll
        fun unstubEco() = unstubEcoConfigFactory()
    }

    private val stringKey =
        PersistentDataKey(namespacedKeyOf("sqlite", "string"), PersistentDataKeyType.STRING, "")
    private val boolKey =
        PersistentDataKey(namespacedKeyOf("sqlite", "bool"), PersistentDataKeyType.BOOLEAN, false)
    private val intKey =
        PersistentDataKey(namespacedKeyOf("sqlite", "int"), PersistentDataKeyType.INT, 0)
    private val doubleKey =
        PersistentDataKey(namespacedKeyOf("sqlite", "double"), PersistentDataKeyType.DOUBLE, 0.0)
    private val decimalKey = PersistentDataKey(
        namespacedKeyOf("sqlite", "decimal"),
        PersistentDataKeyType.BIG_DECIMAL,
        BigDecimal.ZERO
    )
    private val configKey = PersistentDataKey(
        namespacedKeyOf("sqlite", "config"),
        PersistentDataKeyType.CONFIG,
        EcoConfigSection(ConfigType.JSON) as Config
    )
    private val listKey = PersistentDataKey(
        namespacedKeyOf("sqlite", "list"),
        PersistentDataKeyType.STRING_LIST,
        emptyList<String>()
    )

    private fun tempDatabase(): File =
        Files.createTempDirectory("eco-sqlite-test").resolve("nested").resolve("data.db").toFile()

    private fun <T> writingThenReading(
        file: File,
        write: (SQLitePersistentDataHandler) -> Unit,
        read: (SQLitePersistentDataHandler) -> T
    ): T {
        val writer = SQLitePersistentDataHandler(file)
        write(writer)
        writer.shutdown()

        val reader = SQLitePersistentDataHandler(file)
        try {
            return read(reader)
        } finally {
            reader.shutdown()
        }
    }

    @Test
    fun `the database file and its parent directory are created`() {
        val file = tempDatabase()
        SQLitePersistentDataHandler(file).shutdown()

        assertTrue(file.exists(), "expected ${file.absolutePath} to have been created")
    }

    @Test
    fun `every simple key type round trips`() {
        val uuid = UUID.randomUUID()
        val file = tempDatabase()

        val values = writingThenReading(
            file,
            {
                it.write(uuid, stringKey, "hello")
                it.write(uuid, boolKey, true)
                it.write(uuid, intKey, 42)
                it.write(uuid, doubleKey, 1.5)
                it.write(uuid, listKey, listOf("a", "b", "c"))
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

        assertEquals(listOf<Any?>("hello", true, 42, 1.5, listOf("a", "b", "c")), values)
    }

    @Test
    fun `a config value round trips`() {
        val uuid = UUID.randomUUID()
        val file = tempDatabase()

        val stored = writingThenReading(
            file,
            { it.write(uuid, configKey, configOf("nested" to mapOf("value" to 3))) },
            { it.read(uuid, configKey) }
        )

        assertEquals(3, stored?.getInt("nested.value"))
    }

    @Test
    fun `a big decimal keeps its scale`() {
        // Stored as TEXT precisely so that trailing scale survives; a REAL column would report
        // 100.5 here, silently rounding every balance on the server.
        val uuid = UUID.randomUUID()
        val file = tempDatabase()

        val stored = writingThenReading(
            file,
            { it.write(uuid, decimalKey, BigDecimal("100.50")) },
            { it.read(uuid, decimalKey) }
        )

        assertEquals("100.50", stored?.toPlainString())
    }

    @Test
    fun `a big decimal keeps thirty significant digits`() {
        val uuid = UUID.randomUUID()
        val file = tempDatabase()
        val value = BigDecimal("123456789012345678901234567890.1234")

        val stored = writingThenReading(
            file,
            { it.write(uuid, decimalKey, value) },
            { it.read(uuid, decimalKey) }
        )

        assertEquals(value, stored)
    }

    @Test
    fun `shortening a list removes the trailing rows`() {
        val uuid = UUID.randomUUID()
        val file = tempDatabase()

        val writer = SQLitePersistentDataHandler(file)
        writer.write(uuid, listKey, listOf("a", "b", "c"))
        writer.shutdown()

        val rewriter = SQLitePersistentDataHandler(file)
        rewriter.write(uuid, listKey, listOf("a"))
        rewriter.shutdown()

        val reader = SQLitePersistentDataHandler(file)
        val stored = try {
            reader.read(uuid, listKey)
        } finally {
            reader.shutdown()
        }

        assertEquals(listOf("a"), stored)
    }

    @Test
    fun `readAllKeys spans more uuids than one chunk`() {
        val uuids = List(2_500) { UUID.randomUUID() }
        val file = tempDatabase()

        val values = writingThenReading(
            file,
            { handler -> uuids.forEachIndexed { index, uuid -> handler.write(uuid, intKey, index) } },
            { it.readAllKeys(uuids.toSet(), listOf(intKey, stringKey)) }
        )

        assertEquals(uuids.size, values[intKey]?.size)
        assertEquals(emptyMap<UUID, Any>(), values[stringKey])
    }
}
