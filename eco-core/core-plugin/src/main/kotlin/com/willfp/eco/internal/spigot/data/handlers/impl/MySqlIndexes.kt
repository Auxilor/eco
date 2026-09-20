package com.willfp.eco.internal.spigot.data.handlers.impl

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/**
 * Drop [indexName] from [tableName], if this database still has it.
 *
 * Shared by the MySQL and MariaDB handlers, which spell this the same way and differently to
 * SQLite: an index name is scoped to its table, and MySQL has no DROP INDEX IF EXISTS.
 *
 * The index is looked up rather than dropped optimistically because the absent case is the common
 * one -- a database created since these indices left the schema has never had them -- and Exposed
 * retries a failed transaction three times, logging each attempt. Letting the drop fail would
 * print a wall of SQL errors on the first startup of every new server, for something working
 * exactly as intended.
 */
internal fun dropMySqlIndexIfExists(database: Database, tableName: String, indexName: String) {
    transaction(database) {
        val exists = exec(
            """
            SELECT 1 FROM information_schema.STATISTICS
            WHERE table_schema = DATABASE()
              AND table_name = '$tableName'
              AND index_name = '$indexName'
            LIMIT 1
            """.trimIndent()
        ) { it.next() } ?: false

        if (exists) {
            exec("ALTER TABLE $tableName DROP INDEX $indexName")
        }
    }
}
