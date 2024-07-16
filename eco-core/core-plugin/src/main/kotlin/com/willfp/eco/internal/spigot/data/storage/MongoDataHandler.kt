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
        saveKeys(uuid, mapOf(key to value))
    }

    override fun saveKeysFor(uuid: UUID, keys: Map<PersistentDataKey<*>, Any>) {
        saveKeys(uuid, keys)
    }


    private fun saveKeys(uuid: UUID, keys: Map<PersistentDataKey<*>, Any>) {
        val updates = mutableListOf<Bson>()
        keys.forEach { (key, value) ->
            val keyString = key.key.toString()
            when (value) {
                is Int -> updates.add(Updates.set("data.$keyString", value))
                is Long -> updates.add(Updates.set("data.$keyString", value))
                is Double -> updates.add(Updates.set("data.$keyString", value))
                else -> updates.add(Updates.set("data.$keyString", value))
            }
        }

        if (updates.isNotEmpty()) {
            coroutineScope.launch {
                try {
                    val filter = Filters.eq("uuid", uuid.toString())
                    val combinedUpdate = Updates.combine(updates)
                    val options = UpdateOptions().upsert(true)

                    collection.updateOne(filter, combinedUpdate, options)
                } catch (e: Exception) {
                    plugin.logger.severe("Error saving data for player $uuid: ${e.message}")
                }
            }
        }
    }

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