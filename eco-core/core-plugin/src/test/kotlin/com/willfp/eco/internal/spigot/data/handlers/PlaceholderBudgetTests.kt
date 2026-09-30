package com.willfp.eco.internal.spigot.data.handlers

import com.willfp.eco.internal.spigot.data.handlers.impl.MYSQL_PLACEHOLDER_BUDGET
import com.willfp.eco.internal.spigot.data.handlers.impl.SQLITE_PLACEHOLDER_BUDGET
import com.willfp.eco.internal.spigot.data.handlers.impl.UUID_CHUNK_SIZE
import com.willfp.eco.internal.spigot.data.handlers.impl.chunkSizesFor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Exceeding the statement placeholder cap fails at runtime, on somebody else's database, with a
 * driver error that says nothing about leaderboards -- so the arithmetic is pinned here.
 */
class PlaceholderBudgetTests {
    @Test
    fun `a small key count keeps the full uuid chunk`() {
        for (budget in intArrayOf(MYSQL_PLACEHOLDER_BUDGET, SQLITE_PLACEHOLDER_BUDGET)) {
            val (uuidChunk, keyChunk) = chunkSizesFor(10, budget)

            assertEquals(UUID_CHUNK_SIZE, uuidChunk)
            assertEquals(10, keyChunk)
        }
    }

    @Test
    fun `the budget is never exceeded for any key count`() {
        for (budget in intArrayOf(MYSQL_PLACEHOLDER_BUDGET, SQLITE_PLACEHOLDER_BUDGET)) {
            for (keyCount in intArrayOf(1, 10, 100, 1_000, 10_000, 100_000, Int.MAX_VALUE)) {
                val (uuidChunk, keyChunk) = chunkSizesFor(keyCount, budget)

                assertTrue(
                    uuidChunk.toLong() + keyChunk.toLong() <= budget,
                    "budget $budget exceeded for $keyCount keys: $uuidChunk + $keyChunk"
                )
            }
        }
    }

    @Test
    fun `the sqlite budget stays under the sqlite variable limit`() {
        // SQLITE_MAX_VARIABLE_NUMBER is 32766 on 3.32 and later; a statement built at the budget
        // must still fit, or readAllKeys fails on the first leaderboard refresh.
        for (keyCount in intArrayOf(1, 10, 1_000, 100_000)) {
            val (uuidChunk, keyChunk) = chunkSizesFor(keyCount, SQLITE_PLACEHOLDER_BUDGET)

            assertTrue(
                uuidChunk.toLong() + keyChunk.toLong() <= 32_766,
                "sqlite variable limit exceeded for $keyCount keys: $uuidChunk + $keyChunk"
            )
        }
    }

    @Test
    fun `a key count beyond the budget is chunked and still leaves room for uuids`() {
        val (uuidChunk, keyChunk) = chunkSizesFor(100_000, SQLITE_PLACEHOLDER_BUDGET)

        assertTrue(keyChunk < 100_000, "keys must be chunked once they exceed the budget")
        assertTrue(uuidChunk >= 1, "there must always be room for at least one uuid")
    }

    @Test
    fun `chunk sizes are always at least one`() {
        for (budget in intArrayOf(MYSQL_PLACEHOLDER_BUDGET, SQLITE_PLACEHOLDER_BUDGET)) {
            for (keyCount in intArrayOf(0, 1, Int.MAX_VALUE)) {
                val (uuidChunk, keyChunk) = chunkSizesFor(keyCount, budget)

                assertTrue(uuidChunk >= 1, "uuid chunk was $uuidChunk for $keyCount keys")
                assertTrue(keyChunk >= 1, "key chunk was $keyChunk for $keyCount keys")
            }
        }
    }
}
