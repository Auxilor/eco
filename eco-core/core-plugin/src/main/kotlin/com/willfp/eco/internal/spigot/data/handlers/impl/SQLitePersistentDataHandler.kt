package com.willfp.eco.internal.spigot.data.handlers.impl

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.io.File
import java.math.BigDecimal
import java.util.logging.Level
import java.util.logging.Logger
import javax.sql.DataSource
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class SQLitePersistentDataHandler private constructor(
    private val dataSource: DataSource,
    prefix: String
) : ExposedPersistentDataHandler(
    "sqlite",
    dataSource,
    prefix,
    SQLITE_PLACEHOLDER_BUDGET
) {
    constructor(databaseFile: File, prefix: String = "eco_") : this(dataSourceFor(databaseFile), prefix)

    private val logger = Logger.getLogger("eco")

    /**
     * Whether this startup left free pages behind, by dropping an index or rewriting a table.
     *
     * Declared above the init block on purpose: registerSerializers() writes it, and a property
     * declared below would be assigned its initial value again afterwards, erasing the record.
     */
    private var hasFreePages = false

    init {
        registerSerializers()

        // Only ever true on the first startup after an upgrade that changed the schema; from then
        // on there is nothing left to drop or rewrite, and so nothing to reclaim.
        if (hasFreePages) {
            vacuum()
        }
    }

    /**
     * Ask sqlite_master first so the handler knows whether anything was actually reclaimed --
     * DROP INDEX IF EXISTS cannot say, and a VACUUM of a large store is not worth running blind.
     */
    override fun dropIndexIfExists(tableName: String, indexName: String) {
        val existed = transaction(database) {
            exec("SELECT 1 FROM sqlite_master WHERE type = 'index' AND name = '$indexName'") {
                it.next()
            } ?: false
        }

        if (!existed) {
            return
        }

        super.dropIndexIfExists(tableName, indexName)
        hasFreePages = true
    }

    /**
     * Rebuild a table as WITHOUT ROWID, so its rows live in the primary key.
     *
     * SQLite gives every table an implicit integer rowid unless it is told not to, and nothing
     * here has ever read one: a row is identified by (profileUUID, dataKey). Paying for it means
     * the primary key has to be a second b-tree repeating every uuid and key to point back at the
     * rowid, which on a large store is most of the file.
     *
     * There is no ALTER for this, so the table is rewritten. Every step runs in one transaction
     * and SQLite's DDL is transactional, so an interrupted or failed rewrite rolls back to the
     * table as it was. The CREATE statement is taken from the database rather than rebuilt from
     * the Exposed schema so that a table carrying columns or constraints from an older version of
     * eco comes back exactly as it went in.
     */
    override fun clusterOnPrimaryKey(tableName: String) {
        val createStatement = transaction(database) {
            exec("SELECT sql FROM sqlite_master WHERE type = 'table' AND name = '$tableName'") {
                if (it.next()) it.getString(1) else null
            }
        } ?: return

        if (createStatement.contains("WITHOUT ROWID", ignoreCase = true)) {
            return
        }

        val original = "${tableName}_with_rowid"

        transaction(database) {
            // Renaming first means the new table is created from the untouched original statement,
            // rather than one edited to carry a temporary name.
            exec("ALTER TABLE $tableName RENAME TO $original")
            exec("$createStatement WITHOUT ROWID")
            exec("INSERT INTO $tableName SELECT * FROM $original")
            exec("DROP TABLE $original")
        }

        hasFreePages = true
    }

    /**
     * Return the pages a dropped index or a rewritten table freed to the filesystem.
     *
     * Without this the store keeps its old size and merely reuses the free pages as it grows. It
     * runs on a raw connection because VACUUM cannot run inside a transaction, and once only, on
     * a server that is still starting up.
     */
    private fun vacuum() {
        logger.info("Reclaiming space left by a data store upgrade, this may take a moment")

        try {
            dataSource.connection.use { connection ->
                val autoCommit = connection.autoCommit
                connection.autoCommit = true

                try {
                    connection.createStatement().use { it.execute("VACUUM") }
                } finally {
                    connection.autoCommit = autoCommit
                }
            }

            logger.info("Reclaimed space left by a data store upgrade")
        } catch (e: Exception) {
            // Purely an optimisation: the data is correct either way, so a failure here is not
            // worth preventing the server from starting over.
            logger.log(Level.WARNING, "Failed to reclaim space left by a data store upgrade", e)
        }
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
