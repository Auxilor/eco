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
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
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
        val data = text("json_data", eagerLoading = true)
    }

    init {
        transaction(database) {
            SchemaUtils.create(table)
        }

        PersistentDataKeyType.STRING.registerSerializer(this, LegacyMySQLSerializer<String>())
        PersistentDataKeyType.BOOLEAN.registerSerializer(this, LegacyMySQLSerializer<Boolean>())
        PersistentDataKeyType.INT.registerSerializer(this, LegacyMySQLSerializer<Int>())
        PersistentDataKeyType.DOUBLE.registerSerializer(this, LegacyMySQLSerializer<Double>())
        PersistentDataKeyType.BIG_DECIMAL.registerSerializer(this, LegacyMySQLSerializer<BigDecimal>())
        PersistentDataKeyType.CONFIG.registerSerializer(this, LegacyMySQLSerializer<Config>())
        PersistentDataKeyType.STRING_LIST.registerSerializer(this, LegacyMySQLSerializer<List<String>>())
    }

    override fun getSavedUUIDs(): Set<UUID> {
        return transaction(database) {
            table.selectAll()
                .map { it[table.id] }
                .toSet()
        }.map { it.value }.toSet()
    }

    private inner class LegacyMySQLSerializer<T : Any> : DataTypeSerializer<T>() {
        override fun readAsync(uuid: UUID, key: PersistentDataKey<T>): T? {
            val json = transaction(database) {
                table.selectAll()
                    .where { table.id eq uuid }
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

    object Factory : PersistentDataHandlerFactory("legacy_mysql") {
        override fun create(plugin: EcoSpigotPlugin): PersistentDataHandler {
            return LegacyMySQLPersistentDataHandler(plugin.configYml.getSubsection("mysql"))
        }
    }
}
