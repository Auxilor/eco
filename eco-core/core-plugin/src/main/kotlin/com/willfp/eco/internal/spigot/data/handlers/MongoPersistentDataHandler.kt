package com.willfp.eco.internal.spigot.data.handlers

import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.willfp.eco.core.config.Configs
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.handlers.DataTypeSerializer
import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.UUID

class MongoPersistentDataHandler(
    plugin: EcoSpigotPlugin,
    config: Config
) : PersistentDataHandler("mongo") {
    private val client = MongoClient.create(config.getString("url"))
    private val database = client.getDatabase(config.getString("database"))

    // Collection name is set for backwards compatibility
    private val collection = database.getCollection<UUIDProfile>("uuidprofile")

    init {
        PersistentDataKeyType.STRING.registerSerializer(this, MongoSerializer<String>())
        PersistentDataKeyType.BOOLEAN.registerSerializer(this, MongoSerializer<Boolean>())
        PersistentDataKeyType.INT.registerSerializer(this, MongoSerializer<Int>())
        PersistentDataKeyType.DOUBLE.registerSerializer(this, MongoSerializer<Double>())
        PersistentDataKeyType.STRING_LIST.registerSerializer(this, MongoSerializer<List<String>>())

        PersistentDataKeyType.BIG_DECIMAL.registerSerializer(this, object : MongoSerializer<BigDecimal>() {
            override fun convertToMongo(value: BigDecimal): Any {
                return value.toString()
            }

            override fun convertFromMongo(value: Any): BigDecimal {
                return BigDecimal(value.toString())
            }
        })

        PersistentDataKeyType.CONFIG.registerSerializer(this, object : MongoSerializer<Config>() {
            override fun convertToMongo(value: Config): Any {
                return value.toMap()
            }

            @Suppress("UNCHECKED_CAST")
            override fun convertFromMongo(value: Any): Config {
                return Configs.fromMap(value as Map<String, Any>)
            }
        })
    }

    override fun getSavedUUIDs(): Set<UUID> {
        return runBlocking {
            collection.find().toList().map { UUID.fromString(it.uuid) }.toSet()
        }
    }

    private open inner class MongoSerializer<T : Any> : DataTypeSerializer<T>() {
        override fun readAsync(uuid: UUID, key: PersistentDataKey<T>): T? {
            return runBlocking {
                val profile = collection.find(Filters.eq("uuid", uuid.toString())).firstOrNull()
                    ?: return@runBlocking null

                val value = profile.data[key.key.toString()]
                    ?: return@runBlocking null

                convertFromMongo(value)
            }
        }

        protected open fun convertToMongo(value: T): Any {
            return value
        }

        override fun writeAsync(uuid: UUID, key: PersistentDataKey<T>, value: T) {
            runBlocking {
                val profile = collection.find(Filters.eq("uuid", uuid.toString())).firstOrNull()
                    ?: UUIDProfile(uuid.toString(), mutableMapOf())

                profile.data[key.key.toString()] = convertToMongo(value)

                collection.replaceOne(
                    Filters.eq("uuid", uuid.toString()),
                    profile,
                    ReplaceOptions().upsert(true)
                )
            }
        }

        protected open fun convertFromMongo(value: Any): T {
            @Suppress("UNCHECKED_CAST")
            return value as T
        }
    }

    @Serializable
    private data class UUIDProfile(
        // Storing UUID as strings for serialization
        @SerialName("_id") val uuid: String,

        // Storing NamespacedKeys as strings for serialization
        val data: MutableMap<String, @Contextual Any>
    )
}
