package com.willfp.eco.internal.spigot.leaderboard

import com.willfp.eco.core.Eco
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.util.namespacedKeyOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.math.BigDecimal
import java.util.UUID
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * Pins the no-progress rule: a player is ranked only if their stored value is strictly greater
 * than the key's default.
 *
 * Every plugin ranks through this one provider, so the rule is tested once here rather than four
 * times over in four repositories.
 */
class KeyLeaderboardValueProviderTests {
    companion object {
        /** Constructing a [PersistentDataKey] registers it through the eco singleton. */
        @JvmStatic
        @BeforeAll
        fun stubEco() {
            mockkStatic(Eco::class)
            every { Eco.get() } returns mockk(relaxed = true)
        }

        @JvmStatic
        @AfterAll
        fun unstubEco() {
            unmockkStatic(Eco::class)
        }
    }

    private val alice: UUID = UUID.randomUUID()
    private val bob: UUID = UUID.randomUUID()
    private val carol: UUID = UUID.randomUUID()

    private var counter = 0

    /** A fresh key per call: constructing one registers it, so ids must not collide. */
    private fun intKey(default: Int) = PersistentDataKey(
        namespacedKeyOf("kvptest", "level_${counter++}"),
        PersistentDataKeyType.INT,
        default
    )

    @Test
    fun `a value above the default is ranked`() {
        val provider = KeyLeaderboardValueProvider(intKey(1))

        assertEquals(mapOf(alice to 5.0), provider.convert(mapOf(alice to 5)))
    }

    @Test
    fun `a value exactly at the default is unranked`() {
        val provider = KeyLeaderboardValueProvider(intKey(1))

        assertTrue(provider.convert(mapOf(alice to 1)).isEmpty())
    }

    @Test
    fun `a value below the default is unranked`() {
        val provider = KeyLeaderboardValueProvider(intKey(1))

        assertTrue(provider.convert(mapOf(alice to 0)).isEmpty())
    }

    @Test
    fun `an absent value is unranked`() {
        val provider = KeyLeaderboardValueProvider(intKey(1))

        assertFalse(bob in provider.convert(mapOf(alice to 5)))
    }

    @Test
    fun `a zero default excludes zero and negatives, matching the collections filter`() {
        val provider = KeyLeaderboardValueProvider(
            PersistentDataKey(
                namespacedKeyOf("kvptest", "count"),
                PersistentDataKeyType.DOUBLE,
                0.0
            )
        )

        val values = provider.convert(mapOf(alice to 0.0, bob to -3.0, carol to 2.0))

        assertEquals(mapOf(carol to 2.0), values)
    }

    @Test
    fun `a non-zero default excludes players sitting on it`() {
        val provider = KeyLeaderboardValueProvider(
            PersistentDataKey(
                namespacedKeyOf("kvptest", "balance"),
                PersistentDataKeyType.BIG_DECIMAL,
                BigDecimal.valueOf(100)
            )
        )

        val values = provider.convert(
            mapOf(alice to BigDecimal.valueOf(100), bob to BigDecimal.valueOf(101))
        )

        assertEquals(mapOf(bob to 101.0), values)
    }

    @Test
    fun `a non-numeric stored value is unranked rather than ranked as zero`() {
        val provider = KeyLeaderboardValueProvider(intKey(0))

        assertTrue(provider.convert(mapOf(alice to "not a number")).isEmpty())
    }

    @Test
    fun `the ranked key is exposed so a batched sweep can read it`() {
        val key = intKey(1)

        assertEquals(key, KeyLeaderboardValueProvider(key).rankedKey)
    }

    @Test
    fun `a key that can never yield a number is rejected at registration`() {
        assertThrows(IllegalArgumentException::class.java) {
            KeyLeaderboardValueProvider(
                PersistentDataKey(
                    namespacedKeyOf("kvptest", "flag"),
                    PersistentDataKeyType.BOOLEAN,
                    false
                )
            )
        }
    }
}
