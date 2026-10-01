package com.willfp.eco.internal.spigot.data.handlers.impl

import com.willfp.eco.core.config.interfaces.Config
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.math.BigDecimal
import javax.sql.DataSource
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table

/**
 * PostgreSQL has no MEDIUMTEXT: TEXT is already unbounded, so both value column types are TEXT and
 * there is never a legacy table to widen. The base class's DROP INDEX IF EXISTS is already valid
 * here, since index names are scoped to the schema as they are in SQLite.
 *
 * Nor is there anything to cluster: PostgreSQL stores rows in a heap regardless, and CLUSTER is a
 * one-off rewrite that later writes do not maintain.
 */
class PostgreSQLPersistentDataHandler(
    config: Config
) : ExposedPersistentDataHandler(
    "postgresql",
    dataSourceFor(config),
    config.getString("prefix"),
    POSTGRES_PLACEHOLDER_BUDGET,
    config.getInt("connections") - 1
) {
    init {
        registerSerializers()
    }

    override fun Table.textValueColumn(name: String): Column<String> = text(name)

    override fun Table.longTextValueColumn(name: String): Column<String> = text(name)

    override fun bigDecimalSerializer(): ExposedSerializer<BigDecimal> =
        object : DirectStoreSerializer<BigDecimal>() {
            override val table = object : KeyTable<BigDecimal>("big_decimal") {
                // 34 digits of precision, 4 digits of scale
                override val value = decimal(VALUE_COLUMN_NAME, 34, 4)
            }
        }
}

private fun dataSourceFor(config: Config): DataSource = HikariDataSource(HikariConfig().apply {
    driverClassName = "org.postgresql.Driver"
    username = config.getString("user")
    password = config.getString("password")
    jdbcUrl = "jdbc:postgresql://" +
            "${config.getString("host")}:" +
            "${config.getString("port")}/" +
            config.getString("database")
    maximumPoolSize = config.getInt("connections")
})
