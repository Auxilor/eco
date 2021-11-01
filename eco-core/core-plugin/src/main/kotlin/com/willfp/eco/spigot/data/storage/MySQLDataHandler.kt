package com.willfp.eco.spigot.data.storage

import com.willfp.eco.core.Eco
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.spigot.EcoSpigotPlugin
import org.bukkit.NamespacedKey
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.BooleanColumnType
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DoubleColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

@Suppress("UNCHECKED_CAST")
class MySQLDataHandler(
    plugin: EcoSpigotPlugin
) : DataHandler {
    private val columns = mutableMapOf<String, Column<*>>()

    init {
        Database.connect(
            "jdbc:mysql://" +
                    "${plugin.configYml.getString("mysql.host")}:" +
                    "${plugin.configYml.getString("mysql.port")}/" +
                    plugin.configYml.getString("mysql.database"),
            driver = "com.mysql.cj.jdbc.Driver",
            user = plugin.configYml.getString("mysql.user"),
            password = plugin.configYml.getString("mysql.password")
        )

        transaction {
            SchemaUtils.create(Players)
        }
    }

    override fun updateKeys() {
        transaction {
            for (key in Eco.getHandler().keyRegistry.registeredKeys) {
                registerColumn(key, Players)
            }

            SchemaUtils.createMissingTablesAndColumns(Players)
        }
    }

    override fun save() {
        // Do nothing
    }

    override fun <T> write(uuid: UUID, key: NamespacedKey, value: T) {
        transaction {
            getPlayer(uuid)
            val column: Column<T> = getColumn(key.toString()) as Column<T>

            Players.update({ Players.id eq uuid }) {
                it[column] = value
            }
        }
    }

    override fun <T> read(uuid: UUID, key: NamespacedKey): T? {
        var value: T? = null
        transaction {
            val player = getPlayer(uuid)
            value = player[getColumn(key.toString())] as T?
        }
        return value
    }

    object Players : UUIDTable("eco_players") {

    }

    private fun <T> registerColumn(key: PersistentDataKey<T>, table: UUIDTable) {
        table.apply {
            when (key.type) {
                PersistentDataKeyType.INT -> registerColumn<Int>(key.key.toString(), IntegerColumnType())
                    .default(key.defaultValue as Int)
                PersistentDataKeyType.DOUBLE -> registerColumn<Double>(key.key.toString(), DoubleColumnType())
                    .default(key.defaultValue as Double)
                PersistentDataKeyType.BOOLEAN -> registerColumn<Boolean>(key.key.toString(), BooleanColumnType())
                    .default(key.defaultValue as Boolean)
                PersistentDataKeyType.STRING -> registerColumn<String>(key.key.toString(), VarCharColumnType(128))
                    .default(key.defaultValue as String)

                else -> throw NullPointerException("Null value found!")
            }
        }
    }

    private fun getColumn(name: String): Column<*> {
        val cached = columns[name]
        if (cached != null) {
            return cached
        }

        columns[name] = Players.columns.stream().filter { it.name == name }.findFirst().get()
        return getColumn(name)
    }

    private fun getPlayer(uuid: UUID): ResultRow {
        Players.select { Players.id eq uuid }.firstOrNull() ?: run {
            Players.insert {
                it[id] = uuid
            }
        }

        return Players.select { Players.id eq uuid }.first()
    }
}