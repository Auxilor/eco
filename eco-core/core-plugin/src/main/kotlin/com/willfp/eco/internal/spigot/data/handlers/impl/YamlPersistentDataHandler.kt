package com.willfp.eco.internal.spigot.data.handlers.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.handlers.DataTypeSerializer
import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.data.handlers.PersistentDataHandlerFactory
import java.math.BigDecimal
import java.util.UUID
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Reads profile data out of data.yml.
 *
 * yaml is no longer a data handler; this exists only as a migration source, so writing through it
 * throws rather than storing values in a file nothing reads back.
 */
class YamlPersistentDataHandler(
    private val dataYml: Config
) : PersistentDataHandler("yaml") {
    private val logger = Logger.getLogger("eco")

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
        val section = dataYml.getSubsectionOrNull("player") ?: return emptySet()

        // A hand-edited data.yml can carry a key that is not a uuid, and this is the first call
        // every migration makes -- so one bad key must not fail the sweep with the server locked.
        return section.getKeys(false).mapNotNullTo(mutableSetOf()) { key ->
            try {
                UUID.fromString(key)
            } catch (e: IllegalArgumentException) {
                logger.log(Level.WARNING, "Skipping unparseable profile key in data.yml: $key")
                null
            }
        }
    }

    override fun <T> readAll(uuids: Set<UUID>, key: PersistentDataKey<T>): Map<UUID, T> {
        @Suppress("UNCHECKED_CAST")
        val serializer = key.type.getSerializer(this) as YamlSerializer<Any>

        @Suppress("UNCHECKED_CAST")
        return serializer.readAll(uuids, key as PersistentDataKey<Any>) as Map<UUID, T>
    }

    override fun shouldAutosave(): Boolean {
        // Nothing here writes, so there is nothing to flush. data.yml's own bookkeeping is saved by
        // the profile writer instead.
        return false
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
            throw UnsupportedOperationException("The yaml handler is a migration source and cannot be written to")
        }
    }

    object Factory : PersistentDataHandlerFactory("yaml") {
        // Deliberately not registered: a config asking for yaml is resolved to sqlite, and this
        // factory is reached only by the migration code.
        override fun create(plugin: EcoSpigotPlugin) = YamlPersistentDataHandler(plugin.dataYml)
    }
}
