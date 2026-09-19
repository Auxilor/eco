package com.willfp.eco.core.data.handlers

import com.willfp.eco.core.Eco
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.util.namespacedKeyOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.util.UUID
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * The base [PersistentDataHandler.readAllKeys] is the behavioural reference that every batched
 * override is written against, so its contract is pinned here rather than only being implied by
 * the overrides that have to match it.
 */
class ReadAllKeysTests {
    companion object {
        /**
         * Constructing a [PersistentDataKey] registers it through the eco singleton, which is
         * null outside a running server, so it has to be stubbed before any key is created.
         */
        @JvmStatic
        @BeforeAll
        fun stubEco() {
            // Relaxed: key registration is the only call this test provokes, and it has no
            // bearing on what readAllKeys does with the values it is handed.
            val eco = mockk<Eco>(relaxed = true)

            mockkStatic(Eco::class)
            every { Eco.get() } returns eco
        }

        @JvmStatic
        @AfterAll
        fun unstubEco() {
            unmockkStatic(Eco::class)
        }
    }

    private val intKey = PersistentDataKey(
        namespacedKeyOf("readallkeys", "int_key"),
        PersistentDataKeyType.INT,
        0
    )

    private val otherKey = PersistentDataKey(
        namespacedKeyOf("readallkeys", "other_key"),
        PersistentDataKeyType.INT,
        0
    )

    private val listKey = PersistentDataKey(
        namespacedKeyOf("readallkeys", "list_key"),
        PersistentDataKeyType.STRING_LIST,
        emptyList<String>()
    )

    private val alice: UUID = UUID.randomUUID()
    private val bob: UUID = UUID.randomUUID()

    /**
     * Reads from a fixed in-memory store, so the base implementation is exercised as written
     * rather than through a mock that would let it silently stop calling readAll.
     */
    private class FakeHandler(
        private val store: Map<PersistentDataKey<*>, Map<UUID, Any>>
    ) : PersistentDataHandler("fake") {
        var readAllCalls = 0
            private set

        override fun getSavedUUIDs(): MutableSet<UUID> =
            store.values.flatMapTo(mutableSetOf()) { it.keys }

        @Suppress("UNCHECKED_CAST")
        override fun <T : Any?> readAll(uuids: Set<UUID>, key: PersistentDataKey<T>): Map<UUID, T> {
            readAllCalls++

            val values = store[key] ?: return emptyMap()

            return values.filterKeys { it in uuids } as Map<UUID, T>
        }
    }

    private fun handler() = FakeHandler(mapOf(intKey to mapOf<UUID, Any>(alice to 5)))

    @Test
    fun `every requested key is present in the result`() {
        val result = handler().readAllKeys(setOf(alice, bob), listOf(intKey, otherKey))

        assertEquals(setOf<PersistentDataKey<*>>(intKey, otherKey), result.keys)
    }

    @Test
    fun `a key stored for nobody maps to an empty map rather than null`() {
        val result = handler().readAllKeys(setOf(alice, bob), listOf(intKey, otherKey))

        assertTrue(result.getValue(otherKey).isEmpty())
    }

    @Test
    fun `uuids with no stored value are omitted rather than defaulted`() {
        val result = handler().readAllKeys(setOf(alice, bob), listOf(intKey))

        assertEquals(mapOf<UUID, Any>(alice to 5), result.getValue(intKey))
    }

    @Test
    fun `values are passed through from readAll without being transformed`() {
        val stored = mapOf<UUID, Any>(alice to listOf("a", "b"))
        val handler = FakeHandler(mapOf(listKey to stored))

        val result = handler.readAllKeys(setOf(alice), listOf(listKey))

        // Whatever readAll decides -- including omitting absent uuids and dropping empty
        // collections -- is what readAllKeys must report, so that a batched override and a
        // per-key read can never disagree about what is stored.
        assertEquals(stored, result.getValue(listKey))
    }

    @Test
    fun `the returned maps are independent of the ones readAll produced`() {
        val handler = handler()

        val result = handler.readAllKeys(setOf(alice, bob), listOf(intKey))

        // Copied rather than wrapped, so a caller mutating its slice cannot corrupt a handler
        // that returned one of its own internal maps.
        (result.getValue(intKey) as MutableMap<UUID, Any>)[bob] = 9

        assertEquals(mapOf<UUID, Any>(alice to 5), handler.readAll(setOf(alice, bob), intKey))
    }

    @Test
    fun `the base implementation reads once per key`() {
        val handler = handler()

        handler.readAllKeys(setOf(alice, bob), listOf(intKey, otherKey))

        assertEquals(2, handler.readAllCalls)
    }

    @Test
    fun `an empty key collection reads nothing`() {
        val handler = handler()

        val result = handler.readAllKeys(setOf(alice, bob), emptyList())

        assertTrue(result.isEmpty())
        assertEquals(0, handler.readAllCalls)
    }
}
