package com.willfp.eco.internal.spigot.data.storage

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.data.EcoProfileHandler
import com.willfp.eco.internal.spigot.data.serverProfileUUID
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.BooleanColumnType
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DoubleColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/*

The MySQL data handler is hot garbage for several reasons:
- Using MySQL on unstructured data: it's being horrifically misused, but that's just how it has to be.
- Can't remove un-needed keys, there's wasted space in the columns everywhere.
- No native support for the STRING_LIST type, instead it 'serializes' the lists with semicolons as separators.
- General lack of flexibility, it's too rigid.

That's why I added the MongoDB handler, it's far, far better suited for what eco does - use it over
MySQL if you can.

Oh, also - I don't really know how this class works. I've rewritten it and hacked it together several ways
in several sessions, and it's basically complete gibberish to me. Adding the STRING_LIST type is probably
the worst bodge I've shipped in production.

 */

@Suppress("UNCHECKED_CAST")
class LegacyMySQLDataHandler(
    plugin: EcoSpigotPlugin,
    handler: EcoProfileHandler
) : DataHandler(HandlerType.LEGACY_MYSQL) {
    private val playerHandler: ImplementedMySQLHandler
    private val serverHandler: ImplementedMySQLHandler

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

        Database.connect(HikariDataSource(config))

        playerHandler = ImplementedMySQLHandler(
            handler,
            UUIDTable("eco_players"),
            plugin
        )

        serverHandler = ImplementedMySQLHandler(
            handler,
            UUIDTable("eco_server"),
            plugin
        )
    }

    override fun <T : Any> read(uuid: UUID, key: PersistentDataKey<T>): T? {
        return applyFor(uuid) {
            it.read(uuid, key)
        }
    }

    override fun <T : Any> write(uuid: UUID, key: PersistentDataKey<T>, value: T) {
        applyFor(uuid) {
            it.write(uuid, key, value)
        }
    }

    override fun saveKeysFor(uuid: UUID, keys: Set<PersistentDataKey<*>>) {
        applyFor(uuid) {
            it.saveKeysForRow(uuid, keys)
        }
    }

    private inline fun <R> applyFor(uuid: UUID, function: (ImplementedMySQLHandler) -> R): R {
        return if (uuid == serverProfileUUID) {
            function(serverHandler)
        } else {
            function(playerHandler)
        }
    }

    override fun initialize() {
        playerHandler.initialize()
        serverHandler.initialize()
    }
}

@Suppress("UNCHECKED_CAST")
private class ImplementedMySQLHandler(
    private val handler: EcoProfileHandler,
    private val table: UUIDTable,
    private val plugin: EcoPlugin
) {
    private val rows = Caffeine.newBuilder()
        .expireAfterWrite(3, TimeUnit.SECONDS)
        .build<UUID, ResultRow>()

    private val threadFactory = ThreadFactoryBuilder().setNameFormat("eco-mysql-thread-%d").build()
    private val executor = Executors.newFixedThreadPool(plugin.configYml.getInt("mysql.threads"), threadFactory)
    val registeredKeys = mutableSetOf<PersistentDataKey<*>>()

    init {
        transaction {
            SchemaUtils.create(table)
        }
    }

    fun initialize() {
        transaction {
            SchemaUtils.createMissingTablesAndColumns(table, withLogs = false)
        }
    }

    fun ensureKeyRegistration(key: PersistentDataKey<*>) {
        if (table.columns.any { it.name == key.key.toString() }) {
            registeredKeys.add(key)
            return
        }

        registerColumn(key)
        registeredKeys.add(key)
    }

    fun <T : Any> write(uuid: UUID, key: PersistentDataKey<T>, value: Any) {
        getRow(uuid)
        doWrite(uuid, key, key.type.constrainSQLTypes(value))
    }

    private fun doWrite(uuid: UUID, key: PersistentDataKey<*>, constrainedValue: Any) {
        val column: Column<Any> = getColumn(key) as Column<Any>

        executor.submit {
            transaction {
                table.update({ table.id eq uuid }) {
                    it[column] = constrainedValue
                }
            }
        }
    }

    fun saveKeysForRow(uuid: UUID, keys: Set<PersistentDataKey<*>>) {
        saveRow(uuid, keys)
    }

    private fun saveRow(uuid: UUID, keys: Set<PersistentDataKey<*>>) {
        val profile = handler.loadGenericProfile(uuid)

        executor.submit {
            transaction {
                getRow(uuid)

                for (key in keys) {
                    doWrite(uuid, key, key.type.constrainSQLTypes(profile.read(key)))
                }
            }
        }
    }

    fun <T> read(uuid: UUID, key: PersistentDataKey<T>): T? {
        val doRead = Callable<T?> {
            transaction {
                val row = getRow(uuid)
                val column = getColumn(key)
                val raw = row[column]
                key.type.fromConstrained(raw)
            }
        }

        ensureKeyRegistration(key) // DON'T DELETE THIS LINE! I know it's covered in getColumn, but I need to do it here as well.

        doRead.call()

        return if (Eco.get().ecoPlugin.configYml.getBool("mysql.async-reads")) {
            executor.submit(doRead).get()
        } else {
            doRead.call()
        }
    }

    private fun <T> registerColumn(key: PersistentDataKey<T>) {
        try {
            transaction {
                try {
                    table.apply {
                        if (table.columns.any { it.name == key.key.toString() }) {
                            return@apply
                        }

                        when (key.type) {
                            PersistentDataKeyType.INT -> registerColumn<Int>(key.key.toString(), IntegerColumnType())
                                .default(key.defaultValue as Int)

                            PersistentDataKeyType.DOUBLE -> registerColumn<Double>(
                                key.key.toString(),
                                DoubleColumnType()
                            ).default(key.defaultValue as Double)

                            PersistentDataKeyType.BOOLEAN -> registerColumn<Boolean>(
                                key.key.toString(),
                                BooleanColumnType()
                            ).default(key.defaultValue as Boolean)

                            PersistentDataKeyType.STRING -> registerColumn<String>(
                                key.key.toString(),
                                VarCharColumnType(512)
                            ).default(key.defaultValue as String)

                            PersistentDataKeyType.STRING_LIST -> registerColumn<String>(
                                key.key.toString(),
                                VarCharColumnType(8192)
                            ).default(PersistentDataKeyType.STRING_LIST.constrainSQLTypes(key.defaultValue as List<String>) as String)

                            PersistentDataKeyType.CONFIG -> throw IllegalArgumentException(
                                "Config Persistent Data Keys are not supported by the legacy MySQL handler!"
                            )

                            else -> throw NullPointerException("Null value found!")
                        }
                    }

                    SchemaUtils.createMissingTablesAndColumns(table, withLogs = false)
                } catch (e: Exception) {
                    plugin.logger.info("MySQL Error 1!")
                    e.printStackTrace()
                    // What's that? Two enormous exception catches? That's right! This code sucks.
                }
            }
        } catch (e: Exception) {
            plugin.logger.info("MySQL Error 2!")
            e.printStackTrace()
            // It might fail. Who cares? This is legacy.
        }
    }

    private fun getColumn(key: PersistentDataKey<*>): Column<*> {
        ensureKeyRegistration(key)

        val name = key.key.toString()

        return table.columns.first { it.name == name }
    }

    private fun getRow(uuid: UUID): ResultRow {
        fun select(uuid: UUID): ResultRow? {
            return transaction {
                table.select { table.id eq uuid }.limit(1).singleOrNull()
            }
        }

        return rows.get(uuid) {
            val row = select(uuid)

            return@get if (row != null) {
                row
            } else {
                transaction {
                    table.insert { it[id] = uuid }
                }
                select(uuid)
            }
        }
    }
}

private fun <T> PersistentDataKeyType<T>.constrainSQLTypes(value: Any): Any {
    return if (this == PersistentDataKeyType.STRING_LIST) {
        @Suppress("UNCHECKED_CAST")
        value as List<String>
        value.joinToString(separator = ";")
    } else {
        value
    }
}

private fun <T> PersistentDataKeyType<T>.fromConstrained(constrained: Any?): T? {
    if (constrained == null) {
        return null
    }

    @Suppress("UNCHECKED_CAST")
    return if (this == PersistentDataKeyType.STRING_LIST) {
        constrained as String
        constrained.split(";").toList()
    } else {
        constrained
    } as T
}
