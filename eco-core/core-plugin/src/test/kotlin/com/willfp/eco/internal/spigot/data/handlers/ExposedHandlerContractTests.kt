package com.willfp.eco.internal.spigot.data.handlers

import com.willfp.eco.core.Eco
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.data.KeyRegistry
import com.willfp.eco.internal.spigot.data.handlers.impl.ExposedPersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.impl.SQLITE_PLACEHOLDER_BUDGET
import com.willfp.eco.internal.spigot.data.handlers.impl.VALUE_COLUMN_NAME
import com.willfp.eco.util.namespacedKeyOf
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.io.File
import java.math.BigDecimal
import java.nio.file.Files
import java.util.UUID
import javax.sql.DataSource
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * The base handler carries every serializer, table and batched read for MySQL, MariaDB and SQLite
 * at once, so a defect here is a defect on all three. It is exercised through a minimal test
 * subclass over a real sqlite file rather than through any production handler, so the contract is
 * pinned independently of dialect choices.
 */
class ExposedHandlerContractTests {
    companion object {
        /**
         * Constructing a [PersistentDataKey] registers it through the eco singleton, and
         * getSavedUUIDs reads that registry back to decide which tables to scan -- so the stub has
         * to route registration where the real implementation routes it.
         */
        @JvmStatic
        @BeforeAll
        fun stubEco() {
            val eco = mockk<Eco>(relaxed = true)
            every { eco.registerPersistentKey(any()) } answers { KeyRegistry.registerKey(firstArg()) }

            mockkStatic(Eco::class)
            every { Eco.get() } returns eco
        }

        @JvmStatic
        @AfterAll
        fun unstubEco() {
            unmockkStatic(Eco::class)
        }
    }

    private class TestHandler(file: File) : ExposedPersistentDataHandler(
        "test",
        testDataSource(file),
        "test_",
        SQLITE_PLACEHOLDER_BUDGET
    ) {
        init {
            registerSerializers()
        }

        override fun Table.textValueColumn(name: String): Column<String> = text(name)

        override fun Table.longTextValueColumn(name: String): Column<String> = text(name)

        override fun bigDecimalSerializer(): ExposedSerializer<BigDecimal> =
            object : SingleValueSerializer<BigDecimal, String>() {
                override val table = object : KeyTable<String>("big_decimal") {
                    override val value = text(VALUE_COLUMN_NAME)
                }

                override fun convertToStored(value: BigDecimal) = value.toPlainString()

                override fun convertFromStored(value: String) = BigDecimal(value)
            }
    }

    private val intKey = PersistentDataKey(namespacedKeyOf("test", "int_key"), PersistentDataKeyType.INT, 0)
    private val otherIntKey = PersistentDataKey(namespacedKeyOf("test", "other_int"), PersistentDataKeyType.INT, 0)
    private val stringKey =
        PersistentDataKey(namespacedKeyOf("test", "string_key"), PersistentDataKeyType.STRING, "")
    private val listKey = PersistentDataKey(
        namespacedKeyOf("test", "list_key"),
        PersistentDataKeyType.STRING_LIST,
        emptyList<String>()
    )

    private fun tempDatabase(): File =
        Files.createTempDirectory("eco-exposed-test").resolve("data.db").toFile()

    /**
     * Writes are dispatched to the handler's own executor, so the only way to know they landed is
     * to shut the handler down; a second handler over the same file does the reading.
     */
    private fun <T> writingThenReading(file: File, write: (TestHandler) -> Unit, read: (TestHandler) -> T): T {
        val writer = TestHandler(file)
        write(writer)
        writer.shutdown()

        val reader = TestHandler(file)
        try {
            return read(reader)
        } finally {
            reader.shutdown()
        }
    }

    @Test
    fun `a single value round trips`() {
        val uuid = UUID.randomUUID()
        val file = tempDatabase()

        val value = writingThenReading(
            file,
            { it.write(uuid, intKey, 42) },
            { it.read(uuid, intKey) }
        )

        assertEquals(42, value)
    }

    @Test
    fun `a list value round trips in order`() {
        val uuid = UUID.randomUUID()
        val file = tempDatabase()

        val value = writingThenReading(
            file,
            { it.write(uuid, listKey, listOf("a", "b", "c")) },
            { it.read(uuid, listKey) }
        )

        assertEquals(listOf("a", "b", "c"), value)
    }

    @Test
    fun `readAll omits uuids with no stored value`() {
        val stored = UUID.randomUUID()
        val absent = UUID.randomUUID()
        val file = tempDatabase()

        val values = writingThenReading(
            file,
            { it.write(stored, intKey, 7) },
            { it.readAll(setOf(stored, absent), intKey) }
        )

        assertEquals(mapOf(stored to 7), values)
    }

    @Test
    fun `readAllKeys returns every requested key`() {
        val uuid = UUID.randomUUID()
        val file = tempDatabase()

        val values = writingThenReading(
            file,
            {
                it.write(uuid, intKey, 1)
                it.write(uuid, stringKey, "hello")
            },
            { it.readAllKeys(setOf(uuid), listOf(intKey, otherIntKey, stringKey)) }
        )

        assertEquals(mapOf(uuid to 1), values[intKey])
        assertEquals(emptyMap<UUID, Any>(), values[otherIntKey])
        assertEquals(mapOf<UUID, Any>(uuid to "hello"), values[stringKey])
    }

    @Test
    fun `readAllKeys spans more uuids than one chunk`() {
        // UUID_CHUNK_SIZE is 1000, so this crosses the chunk boundary the batched read splits on --
        // the case where a dropped chunk would go unnoticed on a small test.
        val uuids = List(2_500) { UUID.randomUUID() }
        val file = tempDatabase()

        val values = writingThenReading(
            file,
            { handler -> uuids.forEachIndexed { index, uuid -> handler.write(uuid, intKey, index) } },
            { it.readAllKeys(uuids.toSet(), listOf(intKey)) }
        )

        assertEquals(uuids.size, values[intKey]?.size)
        assertEquals(2_499, values[intKey]?.get(uuids.last()))
    }

    @Test
    fun `getSavedUUIDs reports every profile with stored data`() {
        val first = UUID.randomUUID()
        val second = UUID.randomUUID()
        val file = tempDatabase()

        val saved = writingThenReading(
            file,
            {
                it.write(first, intKey, 1)
                it.write(second, stringKey, "x")
            },
            { it.getSavedUUIDs() }
        )

        assertEquals(setOf(first, second), saved)
    }
}

private fun testDataSource(file: File): DataSource = HikariDataSource(HikariConfig().apply {
    driverClassName = "org.sqlite.JDBC"
    jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
    maximumPoolSize = 1
})
