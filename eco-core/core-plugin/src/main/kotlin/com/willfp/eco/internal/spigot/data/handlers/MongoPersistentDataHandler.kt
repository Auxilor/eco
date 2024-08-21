package com.willfp.eco.internal.spigot.data.handlers

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.data.handlers.SerializedProfile
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import java.util.UUID
import java.util.concurrent.Executors
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.willfp.eco.core.config.Configs
import com.willfp.eco.internal.spigot.data.storage.UUIDProfile
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.Document

class MongoPersistentDataHandler(
    config: Config,
    plugin: EcoSpigotPlugin
) : PersistentDataHandler("yaml") {

    private val url: String = config.getString("url") ?: error("MongoDB URL not found in config")
    private val databaseName: String = config.getString("database") ?: error("Database name not found in config")
    private val client = MongoClient.create(url)
    private val database = client.getDatabase(databaseName)
    private val collection = database.getCollection<UUIDProfile>("uuidprofile")
    private val executor = Executors.newCachedThreadPool()

    override fun <T: Any> read(uuid: UUID, key: PersistentDataKey<T>): T? {
        return runBlocking {
            doRead(uuid, key)
        }
    }

    private suspend fun <T: Any> doRead(uuid: UUID, key: PersistentDataKey<T>): T? {
        val document = collection.find(Document("uuid", uuid.toString())).firstOrNull() ?: return null
        val data = document.data[key.key.toString()] as? T

        return data
    }

    override fun <T : Any> write(uuid: UUID, key: PersistentDataKey<T>, value: T) {
        executor.submit {
            runBlocking {
                doWrite(uuid, key, value)
            }
        }
    }

    private suspend fun <T : Any> doWrite(uuid: UUID, key: PersistentDataKey<T>, value: T) {
        val document = collection.find(Document("uuid", uuid.toString())).firstOrNull() ?: return null
        document.data[key.key.toString()] = value

        collection.replaceOne(Document("uuid", uuid.toString()), document)
    }

    override fun serializeData(keys: Set<PersistentDataKey<*>>): Set<SerializedProfile> {
        val profiles = mutableSetOf<SerializedProfile>()

        collection.find().forEach { document ->
            val uuid = UUID.fromString(document.getString("uuid"))
            val data = document.get("data") as Document
            val profileData = keys.associateWith { key ->
                when (key.type) {
                    PersistentDataKeyType.STRING -> data.getString(key.key.key)
                    PersistentDataKeyType.BOOLEAN -> data.getBoolean(key.key.key)
                    PersistentDataKeyType.INT -> data.getInteger(key.key.key)
                    PersistentDataKeyType.DOUBLE -> data.getDouble(key.key.key)
                    PersistentDataKeyType.STRING_LIST -> data.getList(key.key.key, String::class.java)
                    PersistentDataKeyType.BIG_DECIMAL -> data.getDecimal128(key.key.key)?.bigDecimalValue()
                    PersistentDataKeyType.CONFIG -> data.get(key.key.key)
                    else -> null
                } ?: key.defaultValue
            }

            profiles.add(SerializedProfile(uuid, profileData as Map<PersistentDataKey<*>, Any>))
        }

        return profiles
    }

    override fun loadProfileData(data: Set<SerializedProfile>) {
        data.forEach { profile ->
            val document = Document("uuid", profile.uuid.toString())
            val profileData = Document()

            profile.data.forEach { (key, value) ->
                profileData.put(key.key.key, value)
            }

            document.put("data", profileData)
            collection.replaceOne(Document("uuid", profile.uuid.toString()), document, com.mongodb.client.model.ReplaceOptions().upsert(true))
        }
    }

    @Serializable
    internal data class UUIDProfile(
        // Storing UUID as strings for serialization
        @SerialName("_id") val uuid: String,
        // Storing NamespacedKeys as strings for serialization
        val data: MutableMap<String, @Contextual Any>
    )
}
