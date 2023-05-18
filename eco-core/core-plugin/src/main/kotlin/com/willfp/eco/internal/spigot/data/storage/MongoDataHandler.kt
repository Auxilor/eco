package com.willfp.eco.internal.spigot.data.storage

import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.data.ProfileHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue
import java.util.UUID

@Suppress("UNCHECKED_CAST")
class MongoDataHandler(
    plugin: EcoSpigotPlugin,
    private val handler: ProfileHandler
) : DataHandler(HandlerType.MONGO) {
    private val client: CoroutineClient
    private val collection: CoroutineCollection<UUIDProfile>

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        System.setProperty(
            "org.litote.mongo.mapping.service",
            "org.litote.kmongo.jackson.JacksonClassMappingTypeService"
        )

        val url = plugin.configYml.getString("mongodb.url")

        client = KMongo.createClient(url).coroutine
        collection = client.getDatabase("eco").getCollection()
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
        val profile = getOrCreateDocument(uuid)

        val newData = profile.data.apply {
            if (value == null) {
                this.remove(key.key.toString())
            } else {
                this[key.key.toString()] = value
            }
        }

        collection.updateOne(UUIDProfile::uuid eq uuid.toString(), setValue(UUIDProfile::data, newData))
    }

    private suspend fun <T> doRead(uuid: UUID, key: PersistentDataKey<T>): T? {
        val profile = collection.findOne(UUIDProfile::uuid eq uuid.toString()) ?: return key.defaultValue
        return profile.data[key.key.toString()] as? T?
    }

    private suspend fun getOrCreateDocument(uuid: UUID): UUIDProfile {
        val profile = collection.findOne(UUIDProfile::uuid eq uuid.toString())
        return if (profile == null) {
            collection.insertOne(
                UUIDProfile(
                    uuid.toString(),
                    mutableMapOf()
                )
            )

            getOrCreateDocument(uuid)
        } else {
            profile
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
}

private data class UUIDProfile(
    // Storing UUID as strings for serialization
    @BsonId
    val uuid: String,
    // Storing NamespacedKeys as strings for serialization
    val data: MutableMap<String, Any>
)
