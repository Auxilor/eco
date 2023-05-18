package com.willfp.eco.internal.spigot.data.storage

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.readConfig
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.data.ProfileHandler
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.math.BigDecimal
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/*
Better than old MySQL data handler, but that's only because it's literally just dumping all the
data into a single text column, containing the contents of the players profile as a Config.

Whatever. At least it works.
 */

@Suppress("UNCHECKED_CAST")
class MySQLDataHandler(
    plugin: EcoSpigotPlugin,
    private val handler: ProfileHandler
) : DataHandler(HandlerType.MYSQL) {
    private val database: Database
    private val table = UUIDTable("eco_data")

    private val rows = Caffeine.newBuilder()
        .expireAfterWrite(3, TimeUnit.SECONDS)
        .build<UUID, ResultRow>()

    private val threadFactory = ThreadFactoryBuilder().setNameFormat("eco-mysql-thread-%d").build()
    private val executor = Executors.newFixedThreadPool(plugin.configYml.getInt("mysql.threads"), threadFactory)

    private val dataColumn: Column<String>
        get() = table.columns.first { it.name == "json_data" } as Column<String>

    init {
        val config = HikariConfig()
        config.driverClassName = "com.mysql.cj.jdbc.Driver"
        config.username = plugin.configYml.getString("mysql.user")
        config.password = plugin.configYml.getString("mysql.password")
        config.jdbcUrl = "jdbc:mysql://" +
                "${plugin.configYml.getString("mysql.host")}:" +
                "${plugin.configYml.getString("mysql.port")}/" +
                plugin.configYml.getString("mysql.database")
        config.maximumPoolSize = plugin.configYml.getInt("mysql.connections")

        database = Database.connect(HikariDataSource(config))

        transaction(database) {
            SchemaUtils.create(table)

            table.apply {
                registerColumn<String>("json_data", TextColumnType())
            }

            SchemaUtils.createMissingTablesAndColumns(table, withLogs = false)
        }
    }

    override fun <T : Any> read(uuid: UUID, key: PersistentDataKey<T>): T? {
        val data = getData(uuid)

        val value: Any? = when (key.type) {
            PersistentDataKeyType.INT -> data.getIntOrNull(key.key.toString())
            PersistentDataKeyType.DOUBLE -> data.getDoubleOrNull(key.key.toString())
            PersistentDataKeyType.STRING -> data.getStringOrNull(key.key.toString())
            PersistentDataKeyType.BOOLEAN -> data.getBoolOrNull(key.key.toString())
            PersistentDataKeyType.STRING_LIST -> data.getStringsOrNull(key.key.toString())
            PersistentDataKeyType.CONFIG -> data.getSubsectionOrNull(key.key.toString())
            PersistentDataKeyType.BIG_DECIMAL -> if (data.has(key.key.toString()))
                BigDecimal(data.getString(key.key.toString())) else null

            else -> null
        }

        return value as? T?
    }

    override fun <T : Any> write(uuid: UUID, key: PersistentDataKey<T>, value: T) {
        val data = getData(uuid)
        data.set(key.key.toString(), value)

        setData(uuid, data)
    }

    override fun saveKeysFor(uuid: UUID, keys: Map<PersistentDataKey<*>, Any>) {
        executor.submit {
            val data = getData(uuid)

            for ((key, value) in keys) {
                data.set(key.key.toString(), value)
            }

            doSetData(uuid, data)
        }
    }

    private fun getData(uuid: UUID): Config {
        val plaintext = transaction(database) {
            val row = rows.get(uuid) {
                val row = table.select { table.id eq uuid }.limit(1).singleOrNull()

                if (row != null) {
                    row
                } else {
                    transaction(database) {
                        table.insert {
                            it[id] = uuid
                            it[dataColumn] = "{}"
                        }
                    }
                    table.select { table.id eq uuid }.limit(1).singleOrNull()
                }
            }

            row.getOrNull(dataColumn) ?: "{}"
        }

        return readConfig(plaintext, ConfigType.JSON)
    }

    private fun setData(uuid: UUID, config: Config) {
        executor.submit {
            doSetData(uuid, config)
        }
    }

    private fun doSetData(uuid: UUID, config: Config) {
        transaction(database) {
            table.update({ table.id eq uuid }) {
                it[dataColumn] = config.toPlaintext()
            }
        }
    }

    override fun initialize() {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(table, withLogs = false)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        return other is MySQLDataHandler
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }
}
