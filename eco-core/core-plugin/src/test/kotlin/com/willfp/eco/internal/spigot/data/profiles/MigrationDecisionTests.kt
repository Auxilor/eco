package com.willfp.eco.internal.spigot.data.profiles

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Getting this matrix wrong either skips a migration -- silently losing every local key on a MySQL
 * server -- or fires one on every boot, so each row is pinned.
 */
class MigrationDecisionTests {
    private fun decide(
        migrationEnabled: Boolean = true,
        hasPreviousHandler: Boolean = true,
        previousHandlerId: String? = "sqlite",
        defaultHandlerId: String = "sqlite",
        defaultIsMySQL: Boolean = false,
        defaultIsMongoDB: Boolean = false,
        legacyMigrated: Boolean = true,
        localMigrated: Boolean = true,
        hasStoredProfiles: Boolean = false
    ) = decideMigration(
        migrationEnabled,
        hasPreviousHandler,
        previousHandlerId,
        defaultHandlerId,
        defaultIsMySQL,
        defaultIsMongoDB,
        legacyMigrated,
        localMigrated,
        hasStoredProfiles
    )

    @Test
    fun `migration disabled means nothing fires`() {
        assertEquals(
            MigrationDecision(MigrationKind.NONE),
            decide(migrationEnabled = false, previousHandlerId = "yaml", hasStoredProfiles = true)
        )
    }

    @Test
    fun `a first install fires nothing`() {
        assertEquals(
            MigrationDecision(MigrationKind.NONE),
            decide(hasPreviousHandler = false, localMigrated = false)
        )
    }

    @Test
    fun `a yaml server migrates its whole store into sqlite`() {
        assertEquals(
            MigrationDecision(MigrationKind.DEFAULT_HANDLER, "yaml"),
            decide(previousHandlerId = "yaml", localMigrated = false, hasStoredProfiles = true)
        )
    }

    @Test
    fun `a changed handler migrates from the previous one`() {
        assertEquals(
            MigrationDecision(MigrationKind.DEFAULT_HANDLER, "mysql"),
            decide(previousHandlerId = "mysql", defaultHandlerId = "mongodb", defaultIsMongoDB = true)
        )
    }

    @Test
    fun `an unmigrated legacy mysql database is migrated first`() {
        assertEquals(
            MigrationDecision(MigrationKind.LEGACY_MYSQL),
            decide(
                previousHandlerId = "mysql",
                defaultHandlerId = "mysql",
                defaultIsMySQL = true,
                legacyMigrated = false
            )
        )
    }

    @Test
    fun `an unmigrated legacy mongodb database is migrated first`() {
        assertEquals(
            MigrationDecision(MigrationKind.LEGACY_MONGODB),
            decide(
                previousHandlerId = "mongodb",
                defaultHandlerId = "mongodb",
                defaultIsMongoDB = true,
                legacyMigrated = false
            )
        )
    }

    @Test
    fun `a mysql server still migrates the local keys out of data yml`() {
        assertEquals(
            MigrationDecision(MigrationKind.LOCAL_HANDLER, "yaml"),
            decide(
                previousHandlerId = "mysql",
                defaultHandlerId = "mysql",
                defaultIsMySQL = true,
                localMigrated = false,
                hasStoredProfiles = true
            )
        )
    }

    @Test
    fun `an empty data yml needs no local migration`() {
        assertEquals(
            MigrationDecision(MigrationKind.NONE),
            decide(
                previousHandlerId = "mysql",
                defaultHandlerId = "mysql",
                defaultIsMySQL = true,
                localMigrated = false,
                hasStoredProfiles = false
            )
        )
    }

    @Test
    fun `a completed local migration does not fire again`() {
        assertEquals(
            MigrationDecision(MigrationKind.NONE),
            decide(
                previousHandlerId = "mysql",
                defaultHandlerId = "mysql",
                defaultIsMySQL = true,
                localMigrated = true,
                hasStoredProfiles = true
            )
        )
    }

    @Test
    fun `a steady state sqlite server fires nothing`() {
        assertEquals(MigrationDecision(MigrationKind.NONE), decide(hasStoredProfiles = true))
    }
}
