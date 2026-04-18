package com.willfp.eco.internal.spigot.data.handlers.impl

import com.mongodb.MongoClientSettings
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.willfp.eco.core.config.Configs
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.handlers.DataTypeSerializer
import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.BsonArray
import org.bson.BsonBoolean
import org.bson.BsonDecimal128
import org.bson.BsonDocument
import org.bson.BsonDouble
import org.bson.BsonInt32
import org.bson.BsonObjectId
import org.bson.BsonString
import org.bson.BsonValue
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import org.bson.types.Decimal128
import java.math.BigDecimal
import java.util.UUID

class MongoDBPersistentDataHandler(
    config: Config
) : PersistentDataHandler("mongo") {
    private val codecRegistry = CodecRegistries.fromRegistries(
        MongoClientSettings.getDefaultCodecRegistry(),
        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
    )

    private val client = MongoClient.create(config.getString("url"))
    private val database = client.getDatabase(config.getString("database"))

    private val collection = database.getCollection<BsonDocument>(config.getString("collection"))
        .withCodecRegistry(codecRegistry)

    init {
        PersistentDataKeyType.STRING.registerSerializer(this, object : MongoSerializer<String>() {
            override fun serialize(value: String): BsonValue {
                return BsonString(value)
            }

            override fun deserialize(value: BsonValue): String {
                return value.asString().value
            }
        })

        PersistentDataKeyType.BOOLEAN.registerSerializer(this, object : MongoSerializer<Boolean>() {
            override fun serialize(value: Boolean): BsonValue {
                return BsonBoolean(value)
            }

            override fun deserialize(value: BsonValue): Boolean {
                return value.asBoolean().value
            }
        })

        PersistentDataKeyType.INT.registerSerializer(this, object : MongoSerializer<Int>() {
            override fun serialize(value: Int): BsonValue {
                return BsonInt32(value)
            }

            override fun deserialize(value: BsonValue): Int {
                return value.asInt32().value
            }
        })

        PersistentDataKeyType.DOUBLE.registerSerializer(this, object : MongoSerializer<Double>() {
            override fun serialize(value: Double): BsonValue {
                return BsonDouble(value)
            }

            override fun deserialize(value: BsonValue): Double {
                return value.asDouble().value
            }
        })

        PersistentDataKeyType.STRING_LIST.registerSerializer(this, object : MongoSerializer<List<String>>() {
            override fun serialize(value: List<String>): BsonValue {
                return BsonArray(value.map { BsonString(it) })
            }

            override fun deserialize(value: BsonValue): List<String> {
                return value.asArray().values.map { it.asString().value }
            }
        })

        PersistentDataKeyType.BIG_DECIMAL.registerSerializer(this, object : MongoSerializer<BigDecimal>() {
            override fun serialize(value: BigDecimal): BsonValue {
                return BsonDecimal128(Decimal128(value))
            }

            override fun deserialize(value: BsonValue): BigDecimal {
                return value.asDecimal128().value.bigDecimalValue()
            }
        })

        PersistentDataKeyType.CONFIG.registerSerializer(this, object : MongoSerializer<Config>() {
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

            private fun serializeConfigValue(value: Any): BsonValue {
                return when (value) {
                    is String -> BsonString(value)
                    is Int -> BsonInt32(value)
                    is Double -> BsonDouble(value)
                    is Boolean -> BsonBoolean(value)
                    is BigDecimal -> BsonDecimal128(Decimal128(value))
                    is List<*> -> BsonArray(value.map { serializeConfigValue(it!!) })
                    is Map<*, *> -> BsonDocument().apply {
                        value.forEach { (k, v) -> append(k.toString(), serializeConfigValue(v!!)) }
                    }

                    else -> throw IllegalArgumentException("Could not serialize config value type ${value::class.simpleName}")
                }
            }

            override fun serialize(value: Config): BsonValue {
                return serializeConfigValue(value.toMap())
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
                UUID.fromString(it.getString("uuid").value)
            }.toSet()
        }
    }

    private abstract inner class MongoSerializer<T : Any> : DataTypeSerializer<T>() {
        override fun readAsync(uuid: UUID, key: PersistentDataKey<T>): T? {
            return runBlocking {
                val filter = Filters.eq("uuid", uuid.toString())

                val profile = collection.find(filter)
                    .firstOrNull() ?: return@runBlocking null

                val value = profile[key.key.toString()] ?: return@runBlocking null

                deserialize(value)
            }
        }

        override fun writeAsync(uuid: UUID, key: PersistentDataKey<T>, value: T) {
            runBlocking {
                val filter = Filters.eq("uuid", uuid.toString())

                val profile = collection.find(filter).firstOrNull()
                    ?: BsonDocument()
                        .append("_id", BsonObjectId())
                        .append("uuid", BsonString(uuid.toString()))

                profile.append(key.key.toString(), serialize(value))

                collection.replaceOne(
                    filter,
                    profile,
                    ReplaceOptions().upsert(true)
                )
            }
        }

        protected abstract fun serialize(value: T): BsonValue
        protected abstract fun deserialize(value: BsonValue): T
    }
}
