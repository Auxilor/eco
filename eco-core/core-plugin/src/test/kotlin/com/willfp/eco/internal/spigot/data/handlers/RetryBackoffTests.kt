package com.willfp.eco.internal.spigot.data.handlers

import com.willfp.eco.internal.spigot.data.handlers.impl.MAX_WRITE_RETRIES
import com.willfp.eco.internal.spigot.data.handlers.impl.RETRY_BACKOFF_CEILING_MILLIS
import com.willfp.eco.internal.spigot.data.handlers.impl.isTransient
import com.willfp.eco.internal.spigot.data.handlers.impl.retryBackoffMillis
import java.sql.SQLDataException
import java.sql.SQLTransientConnectionException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.sqlite.SQLiteErrorCode
import org.sqlite.SQLiteException

/**
 * The old ceiling gave up on a write after roughly 124ms in total, which is shorter than a single
 * contended commit -- so the backoff is pinned rather than left as an expression in a loop.
 */
class RetryBackoffTests {
    @Test
    fun `the backoff grows exponentially`() {
        assertEquals(4L, retryBackoffMillis(2))
        assertEquals(8L, retryBackoffMillis(3))
        assertEquals(64L, retryBackoffMillis(6))
    }

    @Test
    fun `the backoff is capped`() {
        assertEquals(RETRY_BACKOFF_CEILING_MILLIS, retryBackoffMillis(20))
        assertEquals(RETRY_BACKOFF_CEILING_MILLIS, retryBackoffMillis(Int.MAX_VALUE))
    }

    @Test
    fun `the total wait is at least a second`() {
        val total = (2..MAX_WRITE_RETRIES).sumOf { retryBackoffMillis(it) }

        assertTrue(total >= 1_000L, "a dropped write should cost at least a second of retrying, was $total")
    }

    @Test
    fun `lost connections are retried`() {
        assertTrue(RuntimeException(SQLTransientConnectionException("timed out")).isTransient)
        assertTrue(SQLiteException("busy", SQLiteErrorCode.SQLITE_BUSY).isTransient)
    }

    @Test
    fun `bad data is not retried`() {
        assertFalse(RuntimeException(SQLDataException("Data too long")).isTransient)
        assertFalse(SQLiteException("constraint", SQLiteErrorCode.SQLITE_CONSTRAINT).isTransient)
    }
}
