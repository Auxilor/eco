package com.willfp.eco.internal.spigot.data.handlers.impl

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.Configs
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.readConfig
import com.willfp.eco.core.data.handlers.DataTypeSerializer
import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.data.Bookkeeping
import com.willfp.eco.internal.spigot.data.KeyRegistry
import com.willfp.eco.internal.spigot.data.profiles.ProfileExistenceCheck
import java.math.BigDecimal
import java.util.UUID
import java.util.logging.Level
import java.util.logging.Logger
import javax.sql.DataSource
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

internal const val VALUE_COLUMN_NAME = "dataValue"
internal const val UUID_COLUMN_NAME = "profileUUID"
internal const val KEY_COLUMN_NAME = "dataKey"
internal const val INDEX_COLUMN_NAME = "listIndex"

// A dropped write is silent data loss, so the handler waits far longer than the old ~124ms total
// before giving up. On sqlite busy_timeout already blocks instead of throwing, so reaching the end
// of this loop means something other than contention is wrong.
internal const val MAX_WRITE_RETRIES = 10
internal const val RETRY_BACKOFF_CEILING_MILLIS = 2_000L

internal fun retryBackoffMillis(attempt: Int): Long =
    if (attempt >= 11) RETRY_BACKOFF_CEILING_MILLIS
    else minOf(1L shl attempt, RETRY_BACKOFF_CEILING_MILLIS)

/**
 * A persistent data handler backed by a SQL database through Exposed.
 *
 * Every serializer, table and batched read lives here; a subclass supplies only what its dialect
 * does differently -- column types, the placeholder budget its statements fit inside, and the data
 * source itself.
 *
 * A subclass must not hold state that [registerSerializers] reaches, and must call it from its own
 * init block rather than relying on this constructor: the base class is constructed first, so a
 * subclass property would still be null while the serializers were being built.
 */
abstract class ExposedPersistentDataHandler(
    id: String,
    dataSource: DataSource,
    protected val prefix: String,
    private val placeholderBudget: Int
) : PersistentDataHandler(id), ProfileExistenceCheck {
    protected val database: Database =
        Database.connect(dataSource, connectionAutoRegistration = ExposedConnectionImpl())

    private val logger = Logger.getLogger("eco")

    /**
     * eco's own bookkeeping, kept alongside the profiles rather than in a config file.
     *
     * Created on first use: only the local handler is ever asked for it, and on a server whose
     * configured handler is a remote database there is no reason to create the table there too.
     */
    val bookkeeping: Bookkeeping by lazy { ExposedBookkeeping(database, prefix) }

    /** The column type a plain string value is stored in. */
    protected abstract fun Table.textValueColumn(name: String): Column<String>

    /** The column type a serialized config or list entry is stored in, which may be far larger. */
    protected abstract fun Table.longTextValueColumn(name: String): Column<String>

    /** SQLite has no decimal type, so how a [BigDecimal] is stored is left to the dialect. */
    protected abstract fun bigDecimalSerializer(): ExposedSerializer<BigDecimal>

    /**
     * Widen an existing value column, for a table created by an older version of eco.
     *
     * Only MySQL and MariaDB can do this; SQLite has no MODIFY COLUMN, and never needed one.
     */
    protected open fun alterValueColumn(tableName: String, sqlType: String) {
        // Do nothing
    }

    protected fun registerSerializers() {
        PersistentDataKeyType.STRING.registerSerializer(this, object : DirectStoreSerializer<String>() {
            override val table = object : KeyTable<String>("string") {
                override val value = textValueColumn(VALUE_COLUMN_NAME)
            }

            override fun afterCreate() {
                alterValueColumn(table.tableName, "TEXT")
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

        PersistentDataKeyType.BIG_DECIMAL.registerSerializer(this, bigDecimalSerializer().createTable())

        PersistentDataKeyType.CONFIG.registerSerializer(this, object : SingleValueSerializer<Config, String>() {
            override val table = object : KeyTable<String>("config") {
                override val value = longTextValueColumn(VALUE_COLUMN_NAME)
            }

            override fun afterCreate() {
                alterValueColumn(table.tableName, "MEDIUMTEXT")
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
                override val value = longTextValueColumn(VALUE_COLUMN_NAME)
            }

            override fun afterCreate() {
                // Previously, each entry was stored as varchar(255) rather than MEDIUMTEXT
                alterValueColumn(table.tableName, "MEDIUMTEXT")
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
            val serializer = keyType.getSerializer(this) as ExposedSerializer<*>
            savedUUIDs.addAll(serializer.getSavedUUIDs().map { it.toJavaUuid() })
        }

        return savedUUIDs
    }

    override fun hasStoredProfile(uuid: UUID): Boolean {
        // Every type, not just the registered ones: this answers "has this profile been migrated",
        // and a profile whose only rows belong to a plugin that is currently uninstalled has been.
        // Reading it back is a different question, and one the key registry is right to answer.
        return PersistentDataKeyType.values().any {
            (it.getSerializer(this) as ExposedSerializer<*>).hasStoredProfile(uuid)
        }
    }

    override fun <T> readAll(uuids: Set<UUID>, key: PersistentDataKey<T>): Map<UUID, T> {
        @Suppress("UNCHECKED_CAST")
        val serializer = key.type.getSerializer(this) as ExposedSerializer<Any>

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
            val serializer = type.getSerializer(this) as ExposedSerializer<Any>
            val byName = serializer.readAllKeys(uuids, ofType as List<PersistentDataKey<Any>>)

            for (key in ofType) {
                values[key] = byName[key.key.toString()] ?: emptyMap()
            }
        }

        return values
    }

    protected abstract inner class ExposedSerializer<T : Any> : DataTypeSerializer<T>() {
        protected abstract val table: ProfileTable

        @OptIn(ExperimentalUuidApi::class)
        fun getSavedUUIDs(): Set<Uuid> {
            return transaction(database) {
                table.select(table.uuid).map { it[table.uuid] }.toSet()
            }
        }

        @OptIn(ExperimentalUuidApi::class)
        fun hasStoredProfile(uuid: UUID): Boolean {
            return transaction(database) {
                table.select(table.uuid)
                    .where { table.uuid eq uuid.toKotlinUuid() }
                    .limit(1)
                    .any()
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

        fun createTable(): ExposedSerializer<T> {
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
    protected abstract inner class SingleValueSerializer<T : Any, S : Any> : ExposedSerializer<T>() {
        abstract override val table: KeyTable<S>

        abstract fun convertToStored(value: T): S
        abstract fun convertFromStored(value: S): T

        @OptIn(ExperimentalUuidApi::class)
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
            val (uuidChunk, keyChunk) = chunkSizesFor(keyNames.size, placeholderBudget)

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

        @OptIn(ExperimentalUuidApi::class)
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

        @OptIn(ExperimentalUuidApi::class)
        override fun writeAsync(uuid: UUID, key: PersistentDataKey<T>, value: T) {
            withRetries(uuid, key) {
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

    protected abstract inner class DirectStoreSerializer<T : Any> : SingleValueSerializer<T, T>() {
        override fun convertToStored(value: T): T {
            return value
        }

        override fun convertFromStored(value: T): T {
            return value
        }
    }

    protected abstract inner class MultiValueSerializer<T : Any> : ExposedSerializer<List<T>>() {
        abstract override val table: ListKeyTable<T>

        @OptIn(ExperimentalUuidApi::class)
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

        @OptIn(ExperimentalUuidApi::class)
        override fun readAsync(uuid: UUID, key: PersistentDataKey<List<T>>): List<T>? {
            val stored = transaction(database) {
                table.selectAll()
                    .where { (table.uuid eq uuid.toKotlinUuid()) and (table.key eq key.key.toString()) }
                    .orderBy(table.index)
                    .map { it[table.value] }
            }

            return stored
        }

        @OptIn(ExperimentalUuidApi::class)
        override fun writeAsync(uuid: UUID, key: PersistentDataKey<List<T>>, value: List<T>) {
            withRetries(uuid, key) {
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

    protected abstract inner class ProfileTable(name: String) : Table(prefix + name) {
        @OptIn(ExperimentalUuidApi::class)
        val uuid = uuid(UUID_COLUMN_NAME)
    }

    @OptIn(ExperimentalUuidApi::class)
    protected abstract inner class KeyTable<T>(name: String) : ProfileTable(name) {
        val key = varchar(KEY_COLUMN_NAME, 128)
        abstract val value: Column<T>

        override val primaryKey = PrimaryKey(uuid, key)

        init {
            uniqueIndex(uuid, key)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    protected abstract inner class ListKeyTable<T>(name: String) : ProfileTable(name) {
        val key = varchar(KEY_COLUMN_NAME, 128)
        val index = integer(INDEX_COLUMN_NAME)
        abstract val value: Column<T>

        override val primaryKey = PrimaryKey(uuid, key, index)

        init {
            uniqueIndex(uuid, key, index)
        }
    }

    protected fun <T> withRetries(uuid: UUID, key: PersistentDataKey<*>, action: () -> T): T? {
        var retries = 1
        while (true) {
            try {
                return action()
            } catch (e: Exception) {
                e.printStackTrace()
                if (retries > MAX_WRITE_RETRIES) {
                    logger.log(
                        Level.SEVERE,
                        "Gave up writing ${key.key} for $uuid after $MAX_WRITE_RETRIES attempts. " +
                                "This value has been lost.",
                        e
                    )
                    return null
                }
                retries++

                // Exponential backoff, capped so a long outage does not stall the write executor
                // on a single value.
                runBlocking {
                    delay(retryBackoffMillis(retries))
                }
            }
        }
    }
}
