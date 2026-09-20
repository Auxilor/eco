package com.willfp.eco.internal.spigot.data.handlers.impl

import com.willfp.eco.internal.spigot.data.Bookkeeping
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/**
 * [Bookkeeping] kept in the same database the profiles are, in one table of its own.
 *
 * A value is a list of rows ordered by index, and a single value is a list of one, so both shapes
 * share a table. Nothing here is hot -- the whole store is a handful of rows read at boot and
 * written when a migration finishes -- so a write is a delete followed by an insert rather than an
 * upsert that would have to reconcile a shrinking list.
 */
class ExposedBookkeeping(
    private val database: Database,
    prefix: String
) : Bookkeeping {
    private val table = BookkeepingTable(prefix)

    init {
        transaction(database) {
            SchemaUtils.create(table)
        }
    }

    override fun get(key: String): String? = getList(key).firstOrNull()

    override fun set(key: String, value: String?) =
        setList(key, if (value == null) emptyList() else listOf(value))

    override fun getList(key: String): List<String> = transaction(database) {
        table.select(table.entryValue)
            .where { table.entryKey eq key }
            .orderBy(table.entryIndex)
            .map { it[table.entryValue] }
    }

    override fun setList(key: String, values: List<String>) {
        transaction(database) {
            table.deleteWhere { table.entryKey eq key }

            if (values.isNotEmpty()) {
                // Bound outside the lambda: the batch statement is itself a receiver with a
                // `table` of its own, which is the plain Table rather than this one.
                val rows = table

                rows.batchInsert(values.withIndex()) { indexed ->
                    this[rows.entryKey] = key
                    this[rows.entryIndex] = indexed.index
                    this[rows.entryValue] = indexed.value
                }
            }
        }
    }

    override fun keysStartingWith(prefix: String): Set<String> = transaction(database) {
        // Matched here rather than with LIKE: a stored key can contain the wildcards LIKE reads,
        // and escaping them portably needs an ESCAPE clause each dialect spells differently. The
        // whole store is a handful of keys, so the scan costs nothing and cannot match too much.
        table.select(table.entryKey)
            .withDistinct()
            .map { it[table.entryKey] }
            .filterTo(mutableSetOf()) { it.startsWith(prefix) }
    }

    private class BookkeepingTable(prefix: String) : Table("${prefix}bookkeeping") {
        // Named around `index` and `value`, which Table already defines.
        val entryKey = varchar(KEY_COLUMN_NAME, 191)
        val entryIndex = integer(INDEX_COLUMN_NAME)
        val entryValue = text(VALUE_COLUMN_NAME)

        override val primaryKey = PrimaryKey(entryKey, entryIndex)
    }
}
