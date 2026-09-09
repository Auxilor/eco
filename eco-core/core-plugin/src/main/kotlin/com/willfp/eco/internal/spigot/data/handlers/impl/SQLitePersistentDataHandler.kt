package com.willfp.eco.internal.spigot.data.handlers.impl

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.io.File
import java.math.BigDecimal
import javax.sql.DataSource
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table

class SQLitePersistentDataHandler(
    databaseFile: File,
    prefix: String = "eco_"
) : ExposedPersistentDataHandler(
    "sqlite",
    dataSourceFor(databaseFile),
    prefix,
    SQLITE_PLACEHOLDER_BUDGET
) {
    init {
        registerSerializers()
    }

    // SQLite has no MEDIUMTEXT, and TEXT is unbounded here anyway, so both column kinds collapse
    // onto the same type -- and neither ever needs the widening ALTER the MySQL handlers run.
    override fun Table.textValueColumn(name: String): Column<String> = text(name)

    override fun Table.longTextValueColumn(name: String): Column<String> = text(name)

    /**
     * SQLite has no decimal type: Exposed's decimal() emits NUMERIC, which type affinity collapses
     * to REAL, and a balance stored as REAL loses its scale (100.50 reads back as 100.5). Stored as
     * text, both value and scale round trip exactly -- at the cost of the column not being sortable
     * numerically in SQL, which nothing does.
     */
    override fun bigDecimalSerializer(): ExposedSerializer<BigDecimal> =
        object : SingleValueSerializer<BigDecimal, String>() {
            override val table = object : KeyTable<String>("big_decimal") {
                override val value = text(VALUE_COLUMN_NAME)
            }

            override fun convertToStored(value: BigDecimal): String = value.toPlainString()

            override fun convertFromStored(value: String): BigDecimal = BigDecimal(value)
        }
}

private fun dataSourceFor(databaseFile: File): DataSource {
    // Hikari opens a connection during construction, which creates the file but not the directory
    // above it.
    databaseFile.parentFile?.mkdirs()

    return HikariDataSource(HikariConfig().apply {
        driverClassName = "org.sqlite.JDBC"

        // The pragmas ride on the URL rather than connectionInitSql because the driver applies them
        // per connection either way, and a multi-statement init SQL is not portable.
        //
        // WAL lets readers see a consistent snapshot while a write is in flight and makes commits
        // atomic, replacing a save path that could lose the whole store on a crash. NORMAL is WAL's
        // standard pairing: durable against a process crash, with a small window against an OS
        // crash, and no fsync on a per-tick commit. busy_timeout makes the driver block rather than
        // throw while the single writer is held, which is what keeps withRetries out of the picture
        // in normal operation.
        jdbcUrl = "jdbc:sqlite:${databaseFile.absolutePath}?" +
                "journal_mode=WAL&synchronous=NORMAL&busy_timeout=5000"

        // SQLite permits one writer at a time; a larger pool converts waiting into SQLITE_BUSY
        // exceptions without increasing throughput.
        maximumPoolSize = 1
    })
}
