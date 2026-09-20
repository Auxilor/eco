package com.willfp.eco.internal.spigot.data.profiles

enum class MigrationKind {
    NONE,
    DEFAULT_HANDLER,
    LEGACY_MYSQL,
    LEGACY_MONGODB,
    LOCAL_HANDLER
}

data class MigrationDecision(
    val kind: MigrationKind,
    val fromHandlerId: String? = null
)

/**
 * Decide which migration, if any, this boot has to run.
 *
 * The default-handler check comes first: on a yaml server that pass carries the local keys too,
 * because local and default are then the same sqlite instance and the whole key registry is
 * migrated in one go. The local check exists for the servers the default check never fires on --
 * a MySQL or MongoDB server whose previous-handler already matches, yet whose local keys are still
 * sitting in data.yml.
 */
fun decideMigration(
    migrationEnabled: Boolean,
    hasPreviousHandler: Boolean,
    previousHandlerId: String?,
    defaultHandlerId: String,
    defaultIsMySQL: Boolean,
    defaultIsMongoDB: Boolean,
    legacyMigrated: Boolean,
    localMigrated: Boolean,
    hasStoredProfiles: Boolean
): MigrationDecision {
    if (!migrationEnabled || !hasPreviousHandler) {
        return MigrationDecision(MigrationKind.NONE)
    }

    if (previousHandlerId != null && previousHandlerId != defaultHandlerId) {
        return MigrationDecision(MigrationKind.DEFAULT_HANDLER, previousHandlerId)
    }

    if (defaultIsMySQL && !legacyMigrated) {
        return MigrationDecision(MigrationKind.LEGACY_MYSQL)
    }

    if (defaultIsMongoDB && !legacyMigrated) {
        return MigrationDecision(MigrationKind.LEGACY_MONGODB)
    }

    if (!localMigrated && hasStoredProfiles) {
        return MigrationDecision(MigrationKind.LOCAL_HANDLER, "yaml")
    }

    return MigrationDecision(MigrationKind.NONE)
}
