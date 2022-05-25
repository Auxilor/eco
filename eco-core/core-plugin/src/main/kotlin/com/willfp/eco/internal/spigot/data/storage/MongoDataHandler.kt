package com.willfp.eco.internal.spigot.data.storage

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.data.keys.PersistentDataKey
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bson.codecs.pojo.annotations.BsonId
import org.bukkit.NamespacedKey
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue
import java.util.UUID

@Suppress("UNCHECKED_CAST")
class MongoDataHandler(
    plugin: EcoPlugin
) : DataHandler {
    private val client: CoroutineClient
    private val collection: CoroutineCollection<SerializableProfile>

    init {
        val url = plugin.configYml.getString("mongodb.url")

        client = KMongo.createClient(url).coroutine
        collection = client.getDatabase("eco").getCollection()
    }

    override fun saveAll(uuids: Iterable<UUID>) {
        for (uuid in uuids) {
            saveKeysFor(uuid, PersistentDataKey.values())
        }
    }

    override fun <T> write(uuid: UUID, key: NamespacedKey, value: T) {
        runBlocking {
            launch {
                doWrite(uuid, key, value)
            }
        }
    }

    private suspend fun <T> doWrite(uuid: UUID, key: NamespacedKey, value: T) {
        val profile = getOrCreateDocument(uuid)

        val newData = profile.data.apply {
            if (value == null) {
                this.remove(key.toString())
            } else {
                this[key.toString()] = value
            }
        }

        collection.updateOne(SerializableProfile::uuid eq uuid, setValue(SerializableProfile::data, newData))
    }

    override fun saveKeysFor(uuid: UUID, keys: Set<PersistentDataKey<*>>) {
        runBlocking {
            launch {
                for (key in keys) {
                    doWrite(uuid, key.key, read(uuid, key))
                }
            }
        }
    }

    override fun <T> read(uuid: UUID, key: PersistentDataKey<T>): T? {
        return runBlocking {
            doRead(uuid, key)
        }
    }

    private suspend fun <T> doRead(uuid: UUID, key: PersistentDataKey<T>): T? {
        val profile = collection.findOne(SerializableProfile::uuid eq uuid) ?: return key.defaultValue
        return profile.data[key.key.toString()] as? T?
    }

    private suspend fun getOrCreateDocument(uuid: UUID): SerializableProfile {
        val profile = collection.findOne(SerializableProfile::uuid eq uuid)
        return if (profile == null) {
            collection.insertOne(
                SerializableProfile(
                    uuid,
                    mutableMapOf()
                )
            )

            getOrCreateDocument(uuid)
        } else {
            profile
        }
    }
}

private data class SerializableProfile(
    @BsonId
    val uuid: UUID,
    // Storing NamespacedKeys as strings for serialization
    val data: MutableMap<String, Any>
)
