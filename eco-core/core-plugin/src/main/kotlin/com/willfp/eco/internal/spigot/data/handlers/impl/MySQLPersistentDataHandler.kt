package com.willfp.eco.internal.spigot.data.handlers.impl

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.Configs
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.readConfig
import com.willfp.eco.core.data.handlers.DataTypeSerializer
import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import java.math.BigDecimal
import java.util.UUID
import kotlin.math.pow

private const val VALUE_COLUMN_NAME = "dataValue"
private const val UUID_COLUMN_NAME = "profileUUID"
private const val KEY_COLUMN_NAME = "dataKey"
private const val INDEX_COLUMN_NAME = "listIndex"

class MySQLPersistentDataHandler(
    config: Config
) : PersistentDataHandler("mysql") {
    private val dataSource = HikariDataSource(HikariConfig().apply {
        driverClassName = "com.mysql.cj.jdbc.Driver"
        username = config.getString("user")
        password = config.getString("password")
        jdbcUrl = "jdbc:mysql://" +
                "${config.getString("host")}:" +
                "${config.getString("port")}/" +
                config.getString("database")
        maximumPoolSize = config.getInt("connections")
    })

    private val prefix = config.getString("prefix")

    private val database = Database.connect(dataSource)

    init {
        PersistentDataKeyType.STRING.registerSerializer(this, object : DirectStoreSerializer<String>() {
            override val table = object : KeyTable<String>("string") {
                override val value = varchar(VALUE_COLUMN_NAME, 256)
            }
        }.createTable())

        PersistentDataKeyType.BOOLEAN.registerSerializer(this, object : DirectStoreSerializer<Boolean>() {
            override val table = object : KeyTable<Boolean>("boolean") {
                override val value = bool(VALUE_COLUMN_NAME)
            }
        }.createTable())

        PersistentDataKeyType.INT.registerSerializer(this, object : DirectStoreSerializer<Int>() {
            override val table = object : KeyTable<Int>("int") {
                override val value = integer(VALUE_COLUMN_NAME)
            }
        }.createTable())

        PersistentDataKeyType.DOUBLE.registerSerializer(this, object : DirectStoreSerializer<Double>() {
            override val table = object : KeyTable<Double>("double") {
                override val value = double(VALUE_COLUMN_NAME)
            }
        }.createTable())

        PersistentDataKeyType.BIG_DECIMAL.registerSerializer(this, object : DirectStoreSerializer<BigDecimal>() {
            override val table = object : KeyTable<BigDecimal>("big_decimal") {
                // 34 digits of precision, 4 digits of scale
                override val value = decimal(VALUE_COLUMN_NAME, 34, 4)
            }
        }.createTable())

        PersistentDataKeyType.CONFIG.registerSerializer(this, object : SingleValueSerializer<Config, String>() {
            override val table = object : KeyTable<String>("config") {
                override val value = text(VALUE_COLUMN_NAME)
            }

            override fun convertFromStored(value: String): Config {
                return readConfig(value, ConfigType.JSON)
            }

            override fun convertToStored(value: Config): String {
                // Store config as JSON
                return if (value.type == ConfigType.JSON) {
                    value.toPlaintext()
                } else {
                    Configs.fromMap(value.toMap(), ConfigType.JSON).toPlaintext()
                }
            }
        }.createTable())

        PersistentDataKeyType.STRING_LIST.registerSerializer(this, object : MultiValueSerializer<String>() {
            override val table = object : ListKeyTable<String>("string_list") {
                override val value = varchar(VALUE_COLUMN_NAME, 256)
            }
        }.createTable())
    }

    override fun getSavedUUIDs(): Set<UUID> {
        val savedUUIDs = mutableSetOf<UUID>()

        for (keyType in PersistentDataKeyType.values()) {
            val serializer = keyType.getSerializer(this) as MySQLSerializer<*>
            savedUUIDs.addAll(serializer.getSavedUUIDs())
        }

        return savedUUIDs
    }

    private abstract inner class MySQLSerializer<T : Any> : DataTypeSerializer<T>() {
        protected abstract val table: ProfileTable

        fun getSavedUUIDs(): Set<UUID> {
            return transaction(database) {
                table.selectAll().map { it[table.uuid] }.toSet()
            }
        }

        fun createTable(): MySQLSerializer<T> {
            transaction(database) {
                SchemaUtils.create(table)
            }

            return this
        }
    }

    // T is the key type
    // S is the stored value type
    private abstract inner class SingleValueSerializer<T : Any, S : Any> : MySQLSerializer<T>() {
        abstract override val table: KeyTable<S>

        abstract fun convertToStored(value: T): S
        abstract fun convertFromStored(value: S): T

        override fun readAsync(uuid: UUID, key: PersistentDataKey<T>): T? {
            val stored = transaction(database) {
                table.selectAll()
                    .where { (table.uuid eq uuid) and (table.key eq key.key.toString()) }
                    .limit(1)
                    .singleOrNull()
                    ?.get(table.value)
            }

            return stored?.let { convertFromStored(it) }
        }

        override fun writeAsync(uuid: UUID, key: PersistentDataKey<T>, value: T) {
            withRetries {
                transaction(database) {
                    table.upsert {
                        it[table.uuid] = uuid
                        it[table.key] = key.key.toString()
                        it[table.value] = convertToStored(value)
                    }
                }
            }
        }
    }

    private abstract inner class DirectStoreSerializer<T : Any> : SingleValueSerializer<T, T>() {
        override fun convertToStored(value: T): T {
            return value
        }

        override fun convertFromStored(value: T): T {
            return value
        }
    }

    private abstract inner class MultiValueSerializer<T : Any> : MySQLSerializer<List<T>>() {
        abstract override val table: ListKeyTable<T>

        override fun readAsync(uuid: UUID, key: PersistentDataKey<List<T>>): List<T>? {
            val stored = transaction(database) {
                table.selectAll()
                    .where { (table.uuid eq uuid) and (table.key eq key.key.toString()) }
                    .orderBy(table.index)
                    .map { it[table.value] }
            }

            return stored
        }

        override fun writeAsync(uuid: UUID, key: PersistentDataKey<List<T>>, value: List<T>) {
            withRetries {
                transaction(database) {
                    // Remove existing values greater than the new list size
                    table.deleteWhere {
                        (table.uuid eq uuid) and
                                (table.key eq key.key.toString()) and
                                (table.index greaterEq value.size)
                    }

                    // Replace existing values in bounds
                    value.forEachIndexed { index, t ->
                        table.replace {
                            it[table.uuid] = uuid
                            it[table.key] = key.key.toString()
                            it[table.index] = index
                            it[table.value] = t
                        }
                    }
                }
            }
        }
    }

    private abstract inner class ProfileTable(name: String) : Table(prefix + name) {
        val uuid = uuid(UUID_COLUMN_NAME)
    }

    private abstract inner class KeyTable<T>(name: String) : ProfileTable(name) {
        val key = varchar(KEY_COLUMN_NAME, 128)
        abstract val value: Column<T>

        override val primaryKey = PrimaryKey(uuid, key)

        init {
            uniqueIndex(uuid, key)
        }
    }

    private abstract inner class ListKeyTable<T>(name: String) : ProfileTable(name) {
        val key = varchar(KEY_COLUMN_NAME, 128)
        val index = integer(INDEX_COLUMN_NAME)
        abstract val value: Column<T>

        override val primaryKey = PrimaryKey(uuid, key, index)

        init {
            uniqueIndex(uuid, key, index)
        }
    }

    private inline fun <T> withRetries(action: () -> T): T? {
        var retries = 1
        while (true) {
            try {
                return action()
            } catch (e: Exception) {
                if (retries > 5) {
                    return null
                }
                retries++

                // Exponential backoff
                runBlocking {
                    delay(2.0.pow(retries.toDouble()).toLong())
                }
            }
        }
    }
}
