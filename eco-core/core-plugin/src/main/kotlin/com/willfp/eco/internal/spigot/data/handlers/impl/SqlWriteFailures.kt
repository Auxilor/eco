package com.willfp.eco.internal.spigot.data.handlers.impl

import com.willfp.eco.core.data.keys.PersistentDataKey
import java.sql.SQLRecoverableException
import java.sql.SQLTransientException
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger
import org.jetbrains.exposed.v1.core.ColumnDiff
import org.sqlite.SQLiteException

private const val SQLITE_BUSY = 5
private const val SQLITE_LOCKED = 6
private const val SQLITE_PRIMARY_CODE_MASK = 0xFF

/**
 * Whether this failure can pass on its own: a dropped connection, a deadlock or a timeout, or a
 * locked SQLite database.
 */
internal val Throwable.isTransient: Boolean
    get() = generateSequence(this) { it.cause }.any { cause ->
        cause is SQLTransientException ||
            cause is SQLRecoverableException ||
            cause is SQLiteException && cause.resultCode.code and SQLITE_PRIMARY_CODE_MASK in SQLITE_CONTENTION_CODES
    }

private val SQLITE_CONTENTION_CODES = setOf(SQLITE_BUSY, SQLITE_LOCKED)

internal val KEY_COLUMN_DIFF = ColumnDiff(
    nullability = false,
    type = true,
    autoInc = false,
    defaults = false,
    caseSensitiveName = false,
    sizeAndScale = true
)

private val tooLongKeys = ConcurrentHashMap.newKeySet<String>()

/**
 * Whether this key is too long for the key column, logging it the first time it is seen.
 */
internal fun PersistentDataKey<*>.isTooLongToSave(logger: Logger): Boolean {
    val name = key.asString()

    if (name.length <= KEY_COLUMN_LENGTH) {
        return false
    }

    if (tooLongKeys.add(name)) {
        logger.severe("Data key $name is longer than $KEY_COLUMN_LENGTH characters, so it cannot be saved.")
    }

    return true
}
