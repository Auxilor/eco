package com.willfp.eco.internal.spigot.data.handlers.impl

import com.willfp.eco.core.config.interfaces.Config
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.math.BigDecimal
import javax.sql.DataSource
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class MySQLPersistentDataHandler(
    config: Config
) : ExposedPersistentDataHandler(
    "mysql",
    dataSourceFor(config),
    config.getString("prefix"),
    MYSQL_PLACEHOLDER_BUDGET
) {
    init {
        registerSerializers()
    }

    override fun Table.textValueColumn(name: String): Column<String> = text(name)

    override fun Table.longTextValueColumn(name: String): Column<String> = mediumText(name)

    override fun bigDecimalSerializer(): ExposedSerializer<BigDecimal> =
        object : DirectStoreSerializer<BigDecimal>() {
            override val table = object : KeyTable<BigDecimal>("big_decimal") {
                // 34 digits of precision, 4 digits of scale
                override val value = decimal(VALUE_COLUMN_NAME, 34, 4)
            }
        }

    override fun alterValueColumn(tableName: String, sqlType: String) {
        transaction(database) {
            exec("ALTER TABLE $tableName MODIFY COLUMN $VALUE_COLUMN_NAME $sqlType")
        }
    }
}

private fun dataSourceFor(config: Config): DataSource = HikariDataSource(HikariConfig().apply {
    driverClassName = "com.mysql.cj.jdbc.Driver"
    username = config.getString("user")
    password = config.getString("password")
    jdbcUrl = "jdbc:mysql://" +
            "${config.getString("host")}:" +
            "${config.getString("port")}/" +
            config.getString("database")
    maximumPoolSize = config.getInt("connections")
})
