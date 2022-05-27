package com.willfp.eco.internal.spigot.data.storage

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.data.keys.KeyRegistry
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.data.EcoProfileHandler
import com.willfp.eco.internal.spigot.data.KeyHelpers
import com.willfp.eco.internal.spigot.data.serverProfileUUID
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.apache.logging.log4j.Level
import org.bukkit.NamespacedKey
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.BooleanColumnType
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DoubleColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

@Suppress("UNCHECKED_CAST")
class MySQLDataHandler(
    private val plugin: EcoSpigotPlugin,
    handler: EcoProfileHandler
) : DataHandler {
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

        // Get Exposed to shut the hell up
        runCatching {
            exposedLogger::class.java.getDeclaredField("logger").apply { isAccessible = true }
                .apply {
                    get(exposedLogger).apply {
                        this.javaClass.getDeclaredMethod("setLevel", Level::class.java)
                            .invoke(this, Level.OFF)
                    }
                }
        }

        playerHandler = ImplementedMySQLHandler(
            handler,
            UUIDTable("eco_players"),
            plugin,
            plugin.dataYml.getStrings("categorized-keys.player")
                .mapNotNull { KeyHelpers.deserializeFromString(it) }
        )

        serverHandler = ImplementedMySQLHandler(
            handler,
            UUIDTable("eco_server"),
            plugin,
            plugin.dataYml.getStrings("categorized-keys.server")
                .mapNotNull { KeyHelpers.deserializeFromString(it, server = true) }
        )
    }

    override fun saveAll(uuids: Iterable<UUID>) {
        serverHandler.saveAll(uuids.filter { it == serverProfileUUID })
        playerHandler.saveAll(uuids.filter { it != serverProfileUUID })
    }

    override fun <T : Any> write(uuid: UUID, key: PersistentDataKey<T>, value: Any) {
        applyFor(uuid) {
            it.write(uuid, key, value)
        }
    }

    override fun saveKeysFor(uuid: UUID, keys: Set<PersistentDataKey<*>>) {
        applyFor(uuid) {
            it.saveKeysForRow(uuid, keys)
        }
    }

    override fun <T : Any> read(uuid: UUID, key: PersistentDataKey<T>): T? {
        return applyFor(uuid) {
            it.read(uuid, key)
        }
    }

    private inline fun <R> applyFor(uuid: UUID, function: (ImplementedMySQLHandler) -> R): R {
        return if (uuid == serverProfileUUID) {
            function(serverHandler)
        } else {
            function(playerHandler)
        }
    }

    override fun categorize(key: PersistentDataKey<*>, category: KeyRegistry.KeyCategory) {
        if (category == KeyRegistry.KeyCategory.SERVER) {
            serverHandler.ensureKeyRegistration(key.key)
        } else {
            playerHandler.ensureKeyRegistration(key.key)
        }
    }

    override fun save() {
        plugin.dataYml.set(
            "categorized-keys.player",
            playerHandler.registeredKeys.values
                .map { KeyHelpers.serializeToString(it) }
        )
        plugin.dataYml.set(
            "categorized-keys.server",
            serverHandler.registeredKeys.values
                .map { KeyHelpers.serializeToString(it) }
        )
        plugin.dataYml.save()
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
    plugin: EcoPlugin,
    private val knownKeys: Collection<PersistentDataKey<*>>
) {
    private val columns = Caffeine.newBuilder()
        .expireAfterWrite(3, TimeUnit.SECONDS)
        .build<String, Column<*>>()

    private val rows = Caffeine.newBuilder()
        .expireAfterWrite(3, TimeUnit.SECONDS)
        .build<UUID, ResultRow>()

    private val threadFactory = ThreadFactoryBuilder().setNameFormat("eco-mysql-thread-%d").build()
    private val executor = Executors.newFixedThreadPool(plugin.configYml.getInt("mysql.threads"), threadFactory)
    val registeredKeys = ConcurrentHashMap<NamespacedKey, PersistentDataKey<*>>()
    private val currentlyProcessingRegistration = ConcurrentHashMap<NamespacedKey, Future<*>>()

    init {
        transaction {
            SchemaUtils.create(table)
        }
    }

    fun initialize() {
        transaction {
            for (key in knownKeys) {
                registerColumn(key, table)
            }

            SchemaUtils.createMissingTablesAndColumns(table, withLogs = false)
            for (key in knownKeys) {
                registeredKeys[key.key] = key
            }
        }
    }

    fun ensureKeyRegistration(key: NamespacedKey) {
        if (registeredKeys.contains(key)) {
            return
        }

        val persistentKey = Eco.getHandler().keyRegistry.getKeyFrom(key) ?: return

        if (table.columns.any { it.name == key.toString() }) {
            registeredKeys[key] = persistentKey
            return
        }

        val future = currentlyProcessingRegistration[key]

        if (future != null) {
            future.get()
            return
        }

        currentlyProcessingRegistration[key] = executor.submit {
            transaction {
                registerColumn(persistentKey, table)
                SchemaUtils.createMissingTablesAndColumns(table, withLogs = false)
            }
            registeredKeys[key] = persistentKey
            currentlyProcessingRegistration.remove(key)
        }
    }

    fun <T : Any> write(uuid: UUID, key: PersistentDataKey<T>, value: Any) {
        getOrCreateRow(uuid)
        doWrite(uuid, key.key, key.type.constrainSQLTypes(value))
    }

    private fun doWrite(uuid: UUID, key: NamespacedKey, constrainedValue: Any) {
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

    fun saveAll(uuids: Iterable<UUID>) {
        for (uuid in uuids) {
            saveRow(uuid, PersistentDataKey.values())
        }
    }

    private fun saveRow(uuid: UUID, keys: Set<PersistentDataKey<*>>) {
        val profile = handler.loadGenericProfile(uuid)

        executor.submit {
            transaction {
                getOrCreateRow(uuid)

                for (key in keys) {
                    doWrite(uuid, key.key, key.type.constrainSQLTypes(profile.read(key)))
                }
            }
        }
    }

    fun <T> read(uuid: UUID, key: PersistentDataKey<T>): T? {
        val doRead = Callable<T?> {
            var value: T? = null
            transaction {
                val row = getOrCreateRow(uuid)
                value = key.type.fromConstrained(row[getColumn(key.key)])
            }

            return@Callable value
        }

        ensureKeyRegistration(key.key) // DON'T DELETE THIS LINE! I know it's covered in getColumn, but I need to do it here as well.

        return if (Eco.getHandler().ecoPlugin.configYml.getBool("mysql.async-reads")) {
            executor.submit(doRead).get()
        } else {
            doRead.call()
        }
    }

    private fun <T> registerColumn(key: PersistentDataKey<T>, table: UUIDTable) {
        table.apply {
            if (this.columns.stream().anyMatch { it.name == key.key.toString() }) {
                return@apply
            }

            when (key.type) {
                PersistentDataKeyType.INT -> registerColumn<Int>(key.key.toString(), IntegerColumnType())
                    .default(key.defaultValue as Int)
                PersistentDataKeyType.DOUBLE -> registerColumn<Double>(key.key.toString(), DoubleColumnType())
                    .default(key.defaultValue as Double)
                PersistentDataKeyType.BOOLEAN -> registerColumn<Boolean>(key.key.toString(), BooleanColumnType())
                    .default(key.defaultValue as Boolean)
                PersistentDataKeyType.STRING -> registerColumn<String>(key.key.toString(), VarCharColumnType(512))
                    .default(key.defaultValue as String)
                PersistentDataKeyType.STRING_LIST -> registerColumn<String>(key.key.toString(), VarCharColumnType(8192))
                    .default(PersistentDataKeyType.STRING_LIST.constrainSQLTypes(key.defaultValue as List<String>) as String)

                else -> throw NullPointerException("Null value found!")
            }
        }
    }

    private fun getColumn(key: NamespacedKey): Column<*> {
        ensureKeyRegistration(key)

        val name = key.toString()

        return columns.get(name) {
            table.columns.first { it.name == name }
        }
    }

    private fun getOrCreateRow(uuid: UUID): ResultRow {
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
        this
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
