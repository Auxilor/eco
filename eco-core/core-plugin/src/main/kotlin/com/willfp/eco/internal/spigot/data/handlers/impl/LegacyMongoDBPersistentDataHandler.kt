package com.willfp.eco.internal.spigot.data.handlers.impl

import com.mongodb.MongoClientSettings
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.willfp.eco.core.config.Configs
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.handlers.DataTypeSerializer
import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.data.handlers.PersistentDataHandlerFactory
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.BsonArray
import org.bson.BsonBoolean
import org.bson.BsonDecimal128
import org.bson.BsonDocument
import org.bson.BsonDouble
import org.bson.BsonInt32
import org.bson.BsonString
import org.bson.BsonValue
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import java.math.BigDecimal
import java.util.UUID

class LegacyMongoDBPersistentDataHandler(
    config: Config
) : PersistentDataHandler("legacy_mongodb") {
    private val codecRegistry = CodecRegistries.fromRegistries(
        MongoClientSettings.getDefaultCodecRegistry(),
        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
    )

    private val client = MongoClient.create(config.getString("url"))
    private val database = client.getDatabase(config.getString("database"))

    private val collection = database.getCollection<BsonDocument>("uuidprofile")
        .withCodecRegistry(codecRegistry)

    init {
        PersistentDataKeyType.STRING.registerSerializer(this, object : LegacyMongoSerializer<String>() {
            override fun deserialize(value: BsonValue): String {
                return value.asString().value
            }
        })

        PersistentDataKeyType.BOOLEAN.registerSerializer(this, object : LegacyMongoSerializer<Boolean>() {
            override fun deserialize(value: BsonValue): Boolean {
                return value.asBoolean().value
            }
        })

        PersistentDataKeyType.INT.registerSerializer(this, object : LegacyMongoSerializer<Int>() {
            override fun deserialize(value: BsonValue): Int {
                return value.asInt32().value
            }
        })

        PersistentDataKeyType.DOUBLE.registerSerializer(this, object : LegacyMongoSerializer<Double>() {
            override fun deserialize(value: BsonValue): Double {
                return value.asDouble().value
            }
        })

        PersistentDataKeyType.STRING_LIST.registerSerializer(this, object : LegacyMongoSerializer<List<String>>() {
            override fun deserialize(value: BsonValue): List<String> {
                return value.asArray().values.map { it.asString().value }
            }
        })

        PersistentDataKeyType.BIG_DECIMAL.registerSerializer(this, object : LegacyMongoSerializer<BigDecimal>() {
            override fun deserialize(value: BsonValue): BigDecimal {
                return value.asDecimal128().value.bigDecimalValue()
            }
        })

        PersistentDataKeyType.CONFIG.registerSerializer(this, object : LegacyMongoSerializer<Config>() {
            private fun deserializeConfigValue(value: BsonValue): Any {
                return when (value) {
                    is BsonString -> value.value
                    is BsonInt32 -> value.value
                    is BsonDouble -> value.value
                    is BsonBoolean -> value.value
                    is BsonDecimal128 -> value.value.bigDecimalValue()
                    is BsonArray -> value.values.map { deserializeConfigValue(it) }
                    is BsonDocument -> value.mapValues { (_, v) -> deserializeConfigValue(v) }

                    else -> throw IllegalArgumentException("Could not deserialize config value type ${value::class.simpleName}")
                }
            }

            override fun deserialize(value: BsonValue): Config {
                @Suppress("UNCHECKED_CAST")
                return Configs.fromMap(deserializeConfigValue(value.asDocument()) as Map<String, Any>)
            }
        })
    }

    override fun getSavedUUIDs(): Set<UUID> {
        return runBlocking {
            collection.find().toList().map {
                UUID.fromString(it.getString("_id").value)
            }.toSet()
        }
    }

    private abstract inner class LegacyMongoSerializer<T : Any> : DataTypeSerializer<T>() {
        override fun readAsync(uuid: UUID, key: PersistentDataKey<T>): T? {
            return runBlocking {
                val filter = Filters.eq("_id", uuid.toString())

                val profile = collection.find(filter)
                    .firstOrNull() ?: return@runBlocking null

                val dataMap = profile.getDocument("data")
                val value = dataMap[key.key.toString()] ?: return@runBlocking null

                try {
                    return@runBlocking deserialize(value)
                } catch (_: Exception) {
                    null
                }
            }
        }

        override fun writeAsync(uuid: UUID, key: PersistentDataKey<T>, value: T) {
            throw UnsupportedOperationException("Legacy Mongo does not support writing")
        }

        protected abstract fun deserialize(value: BsonValue): T
    }

    object Factory: PersistentDataHandlerFactory("legacy_mongo") {
        override fun create(plugin: EcoSpigotPlugin): PersistentDataHandler {
            return LegacyMongoDBPersistentDataHandler(plugin.configYml.getSubsection("mongodb"))
        }
    }
}
