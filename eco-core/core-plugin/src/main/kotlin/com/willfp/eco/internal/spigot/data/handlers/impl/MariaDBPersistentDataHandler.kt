@file:OptIn(ExperimentalUuidApi::class)

package com.willfp.eco.internal.spigot.data.handlers.impl

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.Configs
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.readConfig
import com.willfp.eco.core.data.handlers.DataTypeSerializer
import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.data.KeyRegistry
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.math.BigDecimal
import java.util.UUID
import kotlin.math.pow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.ExposedConnectionImpl
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.upsert

private const val VALUE_COLUMN_NAME = "dataValue"
private const val UUID_COLUMN_NAME = "profileUUID"
private const val KEY_COLUMN_NAME = "dataKey"
private const val INDEX_COLUMN_NAME = "listIndex"

// MariaDB, like MySQL, caps a prepared statement at 65535 placeholders, and readAll is called
// with the entire playerbase by the leaderboard service, so the uuid predicate is split into
// chunks well below that limit rather than being emitted as one enormous IN (...) list.


class MariaDBPersistentDataHandler(
    config: Config
) : PersistentDataHandler("mariadb") {
    private val dataSource = HikariDataSource(HikariConfig().apply {
        driverClassName = "org.mariadb.jdbc.Driver"
        username = config.getString("user")
        password = config.getString("password")
        jdbcUrl = "jdbc:mariadb://" +
                "${config.getString("host")}:" +
                "${config.getString("port")}/" +
                config.getString("database")
        maximumPoolSize = config.getInt("connections")
    })

    private val prefix = config.getString("prefix")

    private val database = Database.connect(dataSource, connectionAutoRegistration = ExposedConnectionImpl())

    init {
        PersistentDataKeyType.STRING.registerSerializer(this, object : DirectStoreSerializer<String>() {
            override val table = object : KeyTable<String>("string") {
                override val value = text(VALUE_COLUMN_NAME)
            }

            override fun afterCreate() {
                transaction(database) {
                    exec("ALTER TABLE ${table.tableName} MODIFY COLUMN $VALUE_COLUMN_NAME TEXT")
                }
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
                override val value = mediumText(VALUE_COLUMN_NAME)
            }

            override fun afterCreate() {
                transaction(database) {
                    exec("ALTER TABLE ${table.tableName} MODIFY COLUMN $VALUE_COLUMN_NAME MEDIUMTEXT")
                }
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
                override val value = mediumText(VALUE_COLUMN_NAME)
            }

            override fun afterCreate() {
                // Previously, each entry was stored as varchar(255) rather than MEDIUMTEXT
                transaction(database) {
                    exec("ALTER TABLE ${table.tableName} MODIFY COLUMN $VALUE_COLUMN_NAME MEDIUMTEXT")
                }
            }
        }.createTable())
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getSavedUUIDs(): Set<UUID> {
        val savedUUIDs = mutableSetOf<UUID>()

        // Only the types something has actually registered a key for. Every type has its own
        // table, so looping all of them scans tables that are guaranteed to be empty -- on a
        // server using only INT and DOUBLE keys that was four wasted full scans per sweep.
        val types = KeyRegistry.getRegisteredKeys().mapTo(mutableSetOf()) { it.type }

        for (keyType in types) {
            val serializer = keyType.getSerializer(this) as MariaDBSerializer<*>
            savedUUIDs.addAll(serializer.getSavedUUIDs().map { it.toJavaUuid() })
        }

        return savedUUIDs
    }

    override fun <T> readAll(uuids: Set<UUID>, key: PersistentDataKey<T>): Map<UUID, T> {
        @Suppress("UNCHECKED_CAST")
        val serializer = key.type.getSerializer(this) as MariaDBSerializer<Any>

        @Suppress("UNCHECKED_CAST")
        return serializer.readAll(uuids, key as PersistentDataKey<Any>) as Map<UUID, T>
    }

    @Suppress("UNCHECKED_CAST")
    override fun readAllKeys(
        uuids: Set<UUID>,
        keys: Collection<PersistentDataKey<*>>
    ): Map<PersistentDataKey<*>, Map<UUID, Any>> {
        val values = HashMap<PersistentDataKey<*>, Map<UUID, Any>>(keys.size)

        // Grouped by type because each type has its own table; one batched read per table rather
        // than one per key, which is the whole point of reading this way.
        for ((type, ofType) in keys.groupBy { it.type }) {
            val serializer = type.getSerializer(this) as MariaDBSerializer<Any>
            val byName = serializer.readAllKeys(uuids, ofType as List<PersistentDataKey<Any>>)

            for (key in ofType) {
                values[key] = byName[key.key.toString()] ?: emptyMap()
            }
        }

        return values
    }

    private abstract inner class MariaDBSerializer<T : Any> : DataTypeSerializer<T>() {
        protected abstract val table: ProfileTable

        @OptIn(ExperimentalUuidApi::class)
        fun getSavedUUIDs(): Set<Uuid> {
            return transaction(database) {
                table.select(table.uuid).map { it[table.uuid] }.toSet()
            }
        }

        abstract fun readAll(uuids: Set<UUID>, key: PersistentDataKey<T>): Map<UUID, T>

        /**
         * Read several keys of this serializer's type in one pass over the table.
         *
         * Keyed by the key's string form, matching the KEY column, so the caller can map rows back
         * to the [PersistentDataKey] that asked for them.
         */
        abstract fun readAllKeys(uuids: Set<UUID>, keys: List<PersistentDataKey<T>>): Map<String, Map<UUID, T>>

        fun createTable(): MariaDBSerializer<T> {
            transaction(database) {
                SchemaUtils.create(table)
            }

            this.afterCreate()
            return this
        }

        protected open fun afterCreate() {
            // Do nothing
        }
    }

    // T is the key type
    // S is the stored value type
    private abstract inner class SingleValueSerializer<T : Any, S : Any> : MariaDBSerializer<T>() {
        abstract override val table: KeyTable<S>

        abstract fun convertToStored(value: T): S
        abstract fun convertFromStored(value: S): T

        override fun readAll(uuids: Set<UUID>, key: PersistentDataKey<T>): Map<UUID, T> {
            if (uuids.isEmpty()) {
                return emptyMap()
            }

            val profileUUIDs = uuids.map { it.toKotlinUuid() }
            val values = HashMap<UUID, T>(uuids.size)

            transaction(database) {
                for (chunk in profileUUIDs.chunked(UUID_CHUNK_SIZE)) {
                    table.select(table.uuid, table.value)
                        .where { (table.uuid inList chunk) and (table.key eq key.key.toString()) }
                        .forEach { values[it[table.uuid].toJavaUuid()] = convertFromStored(it[table.value]) }
                }
            }

            return values
        }

        @OptIn(ExperimentalUuidApi::class)
        override fun readAllKeys(uuids: Set<UUID>, keys: List<PersistentDataKey<T>>): Map<String, Map<UUID, T>> {
            if (uuids.isEmpty() || keys.isEmpty()) {
                return emptyMap()
            }

            val keyNames = keys.map { it.key.toString() }
            val (uuidChunk, keyChunk) = chunkSizesFor(keyNames.size, MYSQL_PLACEHOLDER_BUDGET)

            val profileUUIDs = uuids.map { it.toKotlinUuid() }
            val values = HashMap<String, MutableMap<UUID, T>>(keyNames.size)

            // Pre-populated so that a key nobody has stored a value for still reports an empty
            // map, and so a row carrying an unexpected key is dropped rather than inventing one.
            for (name in keyNames) {
                values[name] = HashMap()
            }

            transaction(database) {
                for (uuidsInChunk in profileUUIDs.chunked(uuidChunk)) {
                    for (namesInChunk in keyNames.chunked(keyChunk)) {
                        table.select(table.uuid, table.key, table.value)
                            .where { (table.uuid inList uuidsInChunk) and (table.key inList namesInChunk) }
                            .forEach {
                                values[it[table.key]]?.put(
                                    it[table.uuid].toJavaUuid(),
                                    convertFromStored(it[table.value])
                                )
                            }
                    }
                }
            }

            return values
        }

        override fun readAsync(uuid: UUID, key: PersistentDataKey<T>): T? {
            val stored = transaction(database) {
                table.selectAll()
                    .where { (table.uuid eq uuid.toKotlinUuid()) and (table.key eq key.key.toString()) }
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
                        it[table.uuid] = uuid.toKotlinUuid()
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

    private abstract inner class MultiValueSerializer<T : Any> : MariaDBSerializer<List<T>>() {
        abstract override val table: ListKeyTable<T>

        override fun readAll(uuids: Set<UUID>, key: PersistentDataKey<List<T>>): Map<UUID, List<T>> {
            if (uuids.isEmpty()) {
                return emptyMap()
            }

            val profileUUIDs = uuids.map { it.toKotlinUuid() }
            val rows = ArrayList<Pair<UUID, T>>()

            // Every row for a given uuid lands in exactly one chunk, and each chunk is ordered by
            // index, so grouping the concatenated rows still yields each player's list in index
            // order.
            transaction(database) {
                for (chunk in profileUUIDs.chunked(UUID_CHUNK_SIZE)) {
                    table.select(table.uuid, table.index, table.value)
                        .where { (table.uuid inList chunk) and (table.key eq key.key.toString()) }
                        .orderBy(table.index)
                        .mapTo(rows) { it[table.uuid].toJavaUuid() to it[table.value] }
                }
            }

            return rows.groupBy({ it.first }, { it.second })
        }

        override fun readAllKeys(
            uuids: Set<UUID>,
            keys: List<PersistentDataKey<List<T>>>
        ): Map<String, Map<UUID, List<T>>> =
            // List-typed keys are never ranked, so batching them buys nothing and would need a
            // second query shape that orders by index per key. Delegating keeps this identical to
            // readAll, which is the contract readAllKeys has to preserve anyway.
            keys.associate { it.key.toString() to readAll(uuids, it) }

        override fun readAsync(uuid: UUID, key: PersistentDataKey<List<T>>): List<T>? {
            val stored = transaction(database) {
                table.selectAll()
                    .where { (table.uuid eq uuid.toKotlinUuid()) and (table.key eq key.key.toString()) }
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
                        (table.uuid eq uuid.toKotlinUuid()) and
                                (table.key eq key.key.toString()) and
                                (table.index greaterEq value.size)
                    }

                    // Upsert values (insert new or update existing)
                    value.forEachIndexed { index, t ->
                        table.upsert {
                            it[table.uuid] = uuid.toKotlinUuid()
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
                e.printStackTrace()
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
