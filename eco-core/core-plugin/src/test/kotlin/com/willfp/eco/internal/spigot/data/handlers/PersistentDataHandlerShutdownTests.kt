package com.willfp.eco.internal.spigot.data.handlers

import com.willfp.eco.core.data.handlers.DataTypeSerializer
import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.util.namespacedKeyOf
import java.util.Collections
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * Writes to one key commit in the order they were made, and the final save at shutdown runs after
 * every queued write, so nothing written in the last moments before a stop is lost.
 */
class PersistentDataHandlerShutdownTests {
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

    private val key = PersistentDataKey(namespacedKeyOf("shutdown", "int"), PersistentDataKeyType.INT, 0)

    @Test
    fun `writes to a key keep order`() {
        val handler = RecordingHandler(maxDelayMillis = 2)
        val uuid = UUID.randomUUID()

        repeat(200) { handler.write(uuid, key, it) }
        handler.shutdown()

        assertEquals((0 until 200).toList(), handler.written[uuid])
    }

    @Test
    fun `the final save sees all writes`() {
        val handler = RecordingHandler(maxDelayMillis = 20)
        val uuids = List(16) { UUID.randomUUID() }

        uuids.forEach { handler.write(it, key, 1) }
        handler.shutdown()

        assertEquals(16, handler.writtenAtSave)
    }

    private class RecordingHandler(maxDelayMillis: Int) : PersistentDataHandler("recording") {
        val written = ConcurrentHashMap<UUID, MutableList<Int>>()

        @Volatile
        var writtenAtSave = -1

        init {
            PersistentDataKeyType.INT.registerSerializer(this, object : DataTypeSerializer<Int>() {
                override fun readAsync(uuid: UUID, key: PersistentDataKey<Int>): Int? = written[uuid]?.lastOrNull()

                override fun writeAsync(uuid: UUID, key: PersistentDataKey<Int>, value: Int) {
                    Thread.sleep(Random.nextLong(maxDelayMillis + 1L))
                    written.computeIfAbsent(uuid) { Collections.synchronizedList(mutableListOf()) }.add(value)
                }
            })
        }

        override fun getSavedUUIDs(): Set<UUID> = written.keys

        override fun doSave() {
            writtenAtSave = written.values.sumOf { it.size }
        }
    }
}
