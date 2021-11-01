package com.willfp.eco.spigot.data.storage

import com.willfp.eco.core.Eco
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.spigot.EcoSpigotPlugin
import org.bukkit.NamespacedKey
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Suppress("UNCHECKED_CAST")
class MySQLDataHandler(
    plugin: EcoSpigotPlugin
) : DataHandler {
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

        for (key in Eco.getHandler().keyRegistry.registeredKeys) {
            registerColumn(key, Players)
        }

        transaction {
            SchemaUtils.create(Players)
        }
    }

    override fun save() {
        // Do nothing
    }

    override fun <T> write(uuid: UUID, key: NamespacedKey, value: T) {
        transaction {
            Players.select { Players.id eq uuid }.firstOrNull() ?: run {
                Players.insert {
                    it[id] = uuid
                }
            }
            val column: Column<T> =
                Players.columns.stream().filter { it.name == key.toString() }.findFirst().get() as Column<T>
            Players.update({ Players.id eq uuid }) {
                it[column] = value
            }
        }
    }

    override fun <T> read(uuid: UUID, key: NamespacedKey): T? {
        var value: T? = null
        transaction {
            val player = Players.select { Players.id eq uuid }.firstOrNull() ?: return@transaction
            value = player[Players.columns.stream().filter { it.name == key.toString() }.findFirst().get()] as T?
        }
        return value
    }

    object Players : UUIDTable("Eco_Players") {
        override val id: Column<EntityID<UUID>> = uuid("id")
            .entityId()
    }

    private fun <T> registerColumn(key: PersistentDataKey<T>, table: UUIDTable) {
        transaction {
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
    }
}