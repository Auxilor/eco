package com.willfp.eco.internal.spigot.data.handlers.impl

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.readConfig
import com.willfp.eco.core.data.handlers.DataTypeSerializer
import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.data.handlers.PersistentDataHandlerFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.util.UUID

class LegacyMySQLPersistentDataHandler(
    config: Config
) : PersistentDataHandler("legacy_mysql") {
    private val dataSource = HikariDataSource(HikariConfig().apply {
        driverClassName = "com.mysql.cj.jdbc.Driver"
        username = config.getString("user")
        password = config.getString("password")
        jdbcUrl = "jdbc:mysql://" +
                "${config.getString("host")}:" +
                "${config.getString("port")}/" +
                config.getString("database")
        maximumPoolSize = config.getInt("connections")
    })

    private val database = Database.connect(dataSource)

    private val table = object : UUIDTable("eco_data") {
        val data = text("json_data")
    }

    init {
        transaction(database) {
            SchemaUtils.create(table)
        }

        PersistentDataKeyType.STRING.registerSerializer(this, LegacySerializer<String>())
        PersistentDataKeyType.BOOLEAN.registerSerializer(this, LegacySerializer<Boolean>())
        PersistentDataKeyType.INT.registerSerializer(this, LegacySerializer<Int>())
        PersistentDataKeyType.DOUBLE.registerSerializer(this, LegacySerializer<Double>())
        PersistentDataKeyType.BIG_DECIMAL.registerSerializer(this, LegacySerializer<BigDecimal>())
        PersistentDataKeyType.CONFIG.registerSerializer(this, LegacySerializer<Config>())
        PersistentDataKeyType.STRING_LIST.registerSerializer(this, LegacySerializer<List<String>>())
    }

    override fun getSavedUUIDs(): Set<UUID> {
        return transaction(database) {
            table.selectAll()
                .map { it[table.id] }
                .toSet()
        }.map { it.value }.toSet()
    }

    private inner class LegacySerializer<T : Any> : DataTypeSerializer<T>() {
        override fun readAsync(uuid: UUID, key: PersistentDataKey<T>): T? {
            val json = transaction(database) {
                table.select { table.id eq uuid }
                    .limit(1)
                    .singleOrNull()
                    ?.get(table.data)
            }

            if (json == null) {
                return null
            }

            val data = readConfig(json, ConfigType.JSON)

            val value: Any? = when (key.type) {
                PersistentDataKeyType.INT -> data.getIntOrNull(key.key.toString())
                PersistentDataKeyType.DOUBLE -> data.getDoubleOrNull(key.key.toString())
                PersistentDataKeyType.STRING -> data.getStringOrNull(key.key.toString())
                PersistentDataKeyType.BOOLEAN -> data.getBoolOrNull(key.key.toString())
                PersistentDataKeyType.STRING_LIST -> data.getStringsOrNull(key.key.toString())
                PersistentDataKeyType.CONFIG -> data.getSubsectionOrNull(key.key.toString())
                PersistentDataKeyType.BIG_DECIMAL -> data.getBigDecimalOrNull(key.key.toString())

                else -> null
            }

            @Suppress("UNCHECKED_CAST")
            return value as? T?
        }

        override fun writeAsync(uuid: UUID, key: PersistentDataKey<T>, value: T) {
            throw UnsupportedOperationException("Legacy MySQL does not support writing")
        }
    }

    object Factory: PersistentDataHandlerFactory("legacy_mysql") {
        override fun create(plugin: EcoSpigotPlugin): PersistentDataHandler {
            return LegacyMySQLPersistentDataHandler(plugin.configYml.getSubsection("mysql"))
        }
    }
}
