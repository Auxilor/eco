package com.willfp.eco.internal.spigot.data.handlers

import com.willfp.eco.core.Eco
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.data.handlers.impl.YamlPersistentDataHandler
import com.willfp.eco.util.namespacedKeyOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.util.UUID
import org.bukkit.NamespacedKey
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * The yaml handler is now a migration source only, and getSavedUUIDs is the first call every
 * migration makes -- so a single hand-edited key under `player` must not be able to fail the
 * migration with the server locked.
 */
class YamlPersistentDataHandlerTests {
    companion object {
        @JvmStatic
        @BeforeAll
        fun stubEco() {
            val eco = mockk<Eco>(relaxed = true)

            // The stored path of a value is built from the key's string form, so a mocked key
            // would read from a path no fixture can be written against.
            every { eco.createNamespacedKey(any(), any()) } answers {
                NamespacedKey(firstArg<String>(), secondArg<String>())
            }

            mockkStatic(Eco::class)
            every { Eco.get() } returns eco
        }

        @JvmStatic
        @AfterAll
        fun unstubEco() {
            unmockkStatic(Eco::class)
        }
    }

    private val intKey = PersistentDataKey(namespacedKeyOf("yaml", "int"), PersistentDataKeyType.INT, 0)

    private val alice = UUID.randomUUID()
    private val bob = UUID.randomUUID()

    private fun handlerOf(vararg profiles: Pair<String, Map<String, Any>>) =
        YamlPersistentDataHandler(configOf("player" to mapOf(*profiles)))

    @Test
    fun `saved uuids are read from the player section`() {
        val handler = handlerOf(
            alice.toString() to mapOf("yaml:int" to 1),
            bob.toString() to mapOf("yaml:int" to 2)
        )

        assertEquals(setOf(alice, bob), handler.getSavedUUIDs())
    }

    @Test
    fun `an unparseable key is skipped rather than throwing`() {
        val handler = handlerOf(
            alice.toString() to mapOf("yaml:int" to 1),
            "not-a-uuid" to mapOf("yaml:int" to 2)
        )

        assertEquals(setOf(alice), handler.getSavedUUIDs())
    }

    @Test
    fun `an absent player section reports no uuids`() {
        val handler = YamlPersistentDataHandler(configOf())

        assertEquals(emptySet<UUID>(), handler.getSavedUUIDs())
    }

    @Test
    fun `values are still readable`() {
        val handler = handlerOf(alice.toString() to mapOf("yaml:int" to 7))

        assertEquals(7, handler.read(alice, intKey))
    }

    @Test
    fun `writing throws`() {
        val handler = handlerOf(alice.toString() to mapOf("yaml:int" to 7))
        val serializer = intKey.type.getSerializer(handler)

        assertThrows<UnsupportedOperationException> {
            serializer.writeAsync(alice, intKey, 8)
        }
    }

    @Test
    fun `the handler does not autosave`() {
        assertFalse(handlerOf().shouldAutosave())
    }
}
