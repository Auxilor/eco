package com.willfp.eco.internal.spigot.data.storage

import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.data.ProfileHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bson.codecs.pojo.annotations.BsonId
import java.util.UUID
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit

@Suppress("UNCHECKED_CAST")
class MongoDataHandler(
    plugin: EcoSpigotPlugin,
    private val handler: ProfileHandler
) : DataHandler(HandlerType.MONGO) {
    private val plugin: EcoSpigotPlugin
    private val client: MongoClient
    private val collection: MongoCollection<UUIDProfile>

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        System.setProperty(
            "org.litote.mongo.mapping.service",
            "org.litote.kmongo.jackson.JacksonClassMappingTypeService"
        )
        this.plugin = plugin
        val url = plugin.configYml.getString("mongodb.url")


        client = MongoClient.create(url)
        collection = client.getDatabase(plugin.configYml.getString("mongodb.database"))
            .getCollection<UUIDProfile>("uuidprofile") // Compat with jackson mapping
    }

    override fun <T : Any> read(uuid: UUID, key: PersistentDataKey<T>): T? {
        return runBlocking {
            doRead(uuid, key)
        }
    }

    override fun <T : Any> write(uuid: UUID, key: PersistentDataKey<T>, value: T) {
        scope.launch {
            doWrite(uuid, key, value)
        }
    }

    override fun saveKeysFor(uuid: UUID, keys: Map<PersistentDataKey<*>, Any>) {
        scope.launch {
            for ((key, value) in keys) {
                saveKey(uuid, key, value)
            }
        }
    }

    private suspend fun <T : Any> saveKey(uuid: UUID, key: PersistentDataKey<T>, value: Any) {
        val data = value as T
        doWrite(uuid, key, data)
    }

    private suspend fun <T> doWrite(uuid: UUID, key: PersistentDataKey<T>, value: T) {
        val filter = Filters.eq(UUIDProfile::uuid.name, uuid.toString())
        val update = Updates.combine(
            Updates.setOnInsert(UUIDProfile::uuid.name, uuid.toString()),
            Updates.set("${UUIDProfile::data.name}.${key.key}", value)
        )
        val options = UpdateOptions().upsert(true)

        try {
            collection.updateOne(filter, update, options)
        } catch (e: Exception) {
            plugin.logger.severe("Error updating data for player $uuid: ${e.message}")
        }
    }

    private suspend fun <T> doRead(uuid: UUID, key: PersistentDataKey<T>): T? {
        val profile = collection.find<UUIDProfile>(Filters.eq(UUIDProfile::uuid.name, uuid.toString()))
            .firstOrNull() ?: return key.defaultValue
        return profile.data[key.key.toString()] as? T?
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
}

@Serializable
internal data class UUIDProfile(
    // Storing UUID as strings for serialization
    @SerialName("_id") val uuid: String,
    // Storing NamespacedKeys as strings for serialization
    val data: MutableMap<String, @Contextual Any>
)
