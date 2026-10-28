package com.willfp.eco.internal.spigot.data.handlers

import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.data.handlers.impl.SQLitePersistentDataHandler
import com.willfp.eco.util.namespacedKeyOf
import java.io.File
import java.nio.file.Files
import java.util.UUID
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * SQLite does not enforce the length of a varchar, so a key longer than the bounded key column
 * other databases use still saves there.
 */
class SQLiteLongKeyTests {
    companion object {
        @JvmStatic
        @BeforeAll
        fun stubEco() {
            stubEcoConfigFactory()
        }

        @JvmStatic
        @AfterAll
        fun unstubEco() {
            unstubEcoConfigFactory()
        }
    }

    private val longKey = PersistentDataKey(
        namespacedKeyOf(namespace = "longkeys", key = "k".repeat(250)),
        PersistentDataKeyType.STRING,
        ""
    )

    @Test
    fun `a long key round trips`() {
        val file = File(Files.createTempDirectory("eco-long-keys").toFile(), "data.db")
        val uuid = UUID.randomUUID()
        val writer = SQLitePersistentDataHandler(file)

        writer.write(uuid, longKey, "saved")
        writer.shutdown()

        val reader = SQLitePersistentDataHandler(file)
        try {
            assertEquals("saved", reader.read(uuid, longKey))
        } finally {
            reader.shutdown()
        }
    }
}
