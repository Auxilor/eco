package com.willfp.eco.internal.spigot.data.handlers.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.handlers.DataTypeSerializer
import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import java.math.BigDecimal
import java.util.UUID

class YamlPersistentDataHandler(
    plugin: EcoSpigotPlugin
) : PersistentDataHandler("yaml") {
    private val dataYml = plugin.dataYml

    init {
        PersistentDataKeyType.STRING.registerSerializer(this, object : YamlSerializer<String>() {
            override fun read(config: Config, key: String) = config.getStringOrNull(key)
        })

        PersistentDataKeyType.BOOLEAN.registerSerializer(this, object : YamlSerializer<Boolean>() {
            override fun read(config: Config, key: String) = config.getBoolOrNull(key)
        })

        PersistentDataKeyType.INT.registerSerializer(this, object : YamlSerializer<Int>() {
            override fun read(config: Config, key: String) = config.getIntOrNull(key)
        })

        PersistentDataKeyType.DOUBLE.registerSerializer(this, object : YamlSerializer<Double>() {
            override fun read(config: Config, key: String) = config.getDoubleOrNull(key)
        })

        PersistentDataKeyType.STRING_LIST.registerSerializer(this, object : YamlSerializer<List<String>>() {
            override fun read(config: Config, key: String) = config.getStringsOrNull(key)
        })

        PersistentDataKeyType.CONFIG.registerSerializer(this, object : YamlSerializer<Config>() {
            override fun read(config: Config, key: String) = config.getSubsectionOrNull(key)
        })

        PersistentDataKeyType.BIG_DECIMAL.registerSerializer(this, object : YamlSerializer<BigDecimal>() {
            override fun read(config: Config, key: String) = config.getBigDecimalOrNull(key)
        })
    }

    override fun getSavedUUIDs(): Set<UUID> {
        return dataYml.getSubsection("player").getKeys(false)
            .map { UUID.fromString(it) }
            .toSet()
    }

    override fun <T> readAll(uuids: Set<UUID>, key: PersistentDataKey<T>): Map<UUID, T> {
        @Suppress("UNCHECKED_CAST")
        val serializer = key.type.getSerializer(this) as YamlSerializer<Any>

        @Suppress("UNCHECKED_CAST")
        return serializer.readAll(uuids, key as PersistentDataKey<Any>) as Map<UUID, T>
    }

    override fun shouldAutosave(): Boolean {
        return true
    }

    override fun doSave() {
        dataYml.save()
    }

    private abstract inner class YamlSerializer<T: Any>: DataTypeSerializer<T>() {
        protected abstract fun read(config: Config, key: String): T?

        final override fun readAsync(uuid: UUID, key: PersistentDataKey<T>): T? {
            return read(dataYml, "player.$uuid.${key.key}")
        }

        fun readAll(uuids: Set<UUID>, key: PersistentDataKey<T>): Map<UUID, T> {
            return uuids.mapNotNull { uuid ->
                val value = readAsync(uuid, key)

                // List serializers answer with an empty list rather than null for a profile with
                // no stored entries. The SQL handlers omit such a uuid outright, so it is omitted
                // here too, otherwise readAll would mean different things per storage backend.
                if (value == null || (value is Collection<*> && value.isEmpty())) {
                    null
                } else {
                    uuid to value
                }
            }.toMap()
        }

        final override fun writeAsync(uuid: UUID, key: PersistentDataKey<T>, value: T) {
            dataYml.set("player.$uuid.${key.key}", value)
        }
    }
}
