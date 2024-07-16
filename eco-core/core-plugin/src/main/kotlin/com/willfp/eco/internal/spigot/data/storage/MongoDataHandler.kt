package com.willfp.eco.internal.spigot.data.storage

import com.mongodb.client.model.*
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.data.ProfileHandler
import kotlinx.coroutines.*
import org.bson.Document
import org.bson.conversions.Bson
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max

class MongoDataHandler(
    private val plugin: EcoSpigotPlugin,
    private val handler: ProfileHandler
) : DataHandler(HandlerType.MONGO) {
    private lateinit var mongoClient: MongoClient
    private lateinit var collection: MongoCollection<Document>
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        val connectionString = plugin.configYml.getString("mongodb.url")
        val databaseName = plugin.configYml.getString("mongodb.database")

        try {
            mongoClient = MongoClient.create(connectionString)
            val database = mongoClient.getDatabase(databaseName)
            collection = database.getCollection("uuidProfile")
            // Ensure index is created for fast uuid lookups
            coroutineScope.launch {
                collection.createIndex(Indexes.ascending("uuid"))
            }
        } catch (e: Exception) {
            plugin.logger.severe("Failed to initialize MongoDB connection: ${e.message}")
        }
    }

    suspend fun <T : Any> readSuspend(uuid: UUID, key: PersistentDataKey<T>): T? {
        return withContext(Dispatchers.IO) {
            try {
                val filter = Filters.eq("uuid", uuid.toString())
                val update = Updates.setOnInsert("data.${key.key}", key.defaultValue)
                val options = FindOneAndUpdateOptions()
                    .upsert(true)
                    .returnDocument(ReturnDocument.AFTER)

                val document = collection.findOneAndUpdate(filter, update, options)
                plugin.logger.info("Read data for player $uuid: ${document?.toJson()}")
                document?.getEmbedded(listOf("data", key.key.toString()), key.defaultValue::class.java) ?: key.defaultValue
            } catch (e: Exception) {
                plugin.logger.severe("Error reading/initializing data for player $uuid: ${e.message}")
                null
            }
        }
    }


    @Deprecated("Use readSuspend for better performance", ReplaceWith("runBlocking { readSuspend(uuid, key) }"))
    override fun <T : Any> read(uuid: UUID, key: PersistentDataKey<T>): T? {
        return runBlocking { readSuspend(uuid, key) }
    }


    override fun <T : Any> write(uuid: UUID, key: PersistentDataKey<T>, value: T) {
        plugin.logger.info("MongoDataHandler: Write called for $uuid with key ${key.key} and value $value")
        saveKeys(uuid, mapOf(key to value))
    }

    override fun saveKeysFor(uuid: UUID, keys: Map<PersistentDataKey<*>, Any>) {
        plugin.logger.info("MongoDataHandler: SaveKeysFor called for $uuid with ${keys.size} keys")
        saveKeys(uuid, keys)
    }


    private fun saveKeys(uuid: UUID, keys: Map<PersistentDataKey<*>, Any>) {
        val updates = mutableListOf<Bson>()
        plugin.logger.info("MongoDataHandler: SaveKeys called for $uuid with ${keys.size} keys")
        keys.forEach { (key, value) ->
            val keyString = key.key.toString()
            when (value) {
                is Int -> updates.add(Updates.set("data.$keyString", value))
                is Long -> updates.add(Updates.set("data.$keyString", value))
                is Double -> updates.add(Updates.set("data.$keyString", value))
                else -> updates.add(Updates.set("data.$keyString", value))
            }
            plugin.logger.info("MongoDataHandler: SaveKeys added key $keyString with value $value")
        }

        if (updates.isNotEmpty()) {
            coroutineScope.launch {
                try {
                    val filter = Filters.eq("uuid", uuid.toString())
                    val combinedUpdate = Updates.combine(updates)
                    val options = UpdateOptions().upsert(true)

                    val result = collection.updateOne(filter, combinedUpdate, options)
                    plugin.logger.info("Saved ${updates.size} keys for $uuid. Modified: ${result.modifiedCount}, Upserted: ${result.upsertedId}")
                } catch (e: Exception) {
                    plugin.logger.severe("Error saving data for player $uuid: ${e.message}")
                }
            }
        }
    }


//    override fun saveKeysFor(uuid: UUID, keys: Map<PersistentDataKey<*>, Any>) {
//        plugin.logger.info("MongoDatahandler: SaveKeysFor called for $uuid with ${keys.size} keys")
//        keys.forEach { (key, value) ->
//            @Suppress("UNCHECKED_CAST")
//            when (value) {
//                is Int -> write(uuid, key as PersistentDataKey<Int>, value)
//                is Long -> write(uuid, key as PersistentDataKey<Long>, value)
//                is Double -> write(uuid, key as PersistentDataKey<Double>, value)
//                is Boolean -> write(uuid, key as PersistentDataKey<Boolean>, value)
//                is String -> write(uuid, key as PersistentDataKey<String>, value)
//                else -> {
//                    plugin.logger.warning("Unsupported type for key ${key.key}: ${value::class.java}")
//                    write(uuid, key as PersistentDataKey<Any>, value)
//                }
//            }
//        }
//        saveAsync()
//        plugin.logger.info("SaveKeysFor called for $uuid with ${keys.size} keys")
//    }


//    private suspend fun saveAllPendingWrites() {
//        plugin.logger.info("Save all pending writes running")
//        val writesToExecute = HashMap(pendingWrites)
//        if (writesToExecute.isEmpty()) {
//            plugin.logger.info("No pending writes to save")
//            return
//        }
//        pendingWrites.clear()
//
//        writesToExecute.forEach { (uuid, data) ->
//            try {
//                val filter = Filters.eq("uuid", uuid.toString())
//                val updates = mutableListOf<Bson>(Updates.setOnInsert("uuid", uuid.toString()))
//
//                data.forEach { (key, value) ->
//                    updates.add(Updates.set("data.$key", value))
//                }
//
//                val combinedUpdate = Updates.combine(updates)
//                val options = UpdateOptions().upsert(true)
//
//                plugin.logger.info("Saving to DB for $uuid: $data")
//                val result = collection.updateOne(filter, combinedUpdate, options)
//                plugin.logger.info("Save result for $uuid: ${result.modifiedCount} modified, ${result.upsertedId} upserted")
//            } catch (e: Exception) {
//                plugin.logger.severe("Error saving data for player $uuid: ${e.message}")
//                // Re-add failed writes to pendingWrites for retry
//                pendingWrites.merge(uuid, data.toMutableMap()) { old, new -> old.apply { putAll(new) } }
//            }
//        }
//    }

//    private suspend fun saveAllPendingWrites() {
//        plugin.logger.info("")
//        plugin.logger.info("Save all pending writes")
//        val writesToExecute = HashMap(pendingWrites)
//        pendingWrites.clear()
//
//        writesToExecute.forEach { (uuid, data) ->
//            try {
//                plugin.logger.info("Save data for player $uuid: $data")
//                val filter = Filters.eq("uuid", uuid.toString())
//                val update = Updates.combine(
//                    Updates.setOnInsert("uuid", uuid.toString()),
//                    Updates.set("data", data)
//                )
//                val options = UpdateOptions().upsert(true)
//
//                collection.updateOne(filter, update, options)
//            } catch (e: Exception) {
//                plugin.logger.severe("Error saving data for player $uuid: ${e.message}")
//                // TODO: 1. add retry limit (per key?)
//                // TODO: 2. ensure retry key does not overwrite updated value in the meantime (player gains XP while lower xp value is retried)
//                // Re-add failed writes to pendingWrites for retry
//                pendingWrites.merge(uuid, data.toMutableMap()) { old, new -> old.apply { putAll(new) } }
//            }
//        }
//        plugin.logger.info("")
//    }


    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        return other is MongoDataHandler
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }

    // worth calling close on disable?
    fun close() {
        runBlocking {
            coroutineScope.cancel() // Cancel all coroutines
            mongoClient.close()
        }
    }
}