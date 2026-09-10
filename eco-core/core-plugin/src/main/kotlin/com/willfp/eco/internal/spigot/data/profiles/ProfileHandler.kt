package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.ServerLocking
import com.willfp.eco.internal.spigot.data.KeyRegistry
import com.willfp.eco.internal.spigot.data.handlers.PersistentDataHandlerFactory
import com.willfp.eco.internal.spigot.data.handlers.PersistentDataHandlers
import com.willfp.eco.internal.spigot.data.handlers.handlersToShutdown
import com.willfp.eco.internal.spigot.data.handlers.resolveHandlerId
import com.willfp.eco.internal.spigot.data.handlers.sqliteHandlerFor
import com.willfp.eco.internal.spigot.data.handlers.impl.LegacyMongoDBPersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.impl.LegacyMySQLPersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.impl.MongoDBPersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.impl.MySQLPersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.impl.SQLitePersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.impl.YamlPersistentDataHandler
import com.willfp.eco.internal.spigot.data.profiles.impl.EcoPlayerProfile
import com.willfp.eco.internal.spigot.data.profiles.impl.EcoProfile
import com.willfp.eco.internal.spigot.data.profiles.impl.EcoServerProfile
import com.willfp.eco.internal.spigot.data.profiles.impl.serverProfileUUID
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

const val LEGACY_MIGRATED_KEY = "legacy-data-migrated"
const val LOCAL_MIGRATED_KEY = "local-handler-migrated"

// The keys data.yml has already given up, so a key is only ever backfilled once.
const val BACKFILLED_KEYS_KEY = "backfilled-keys"

class ProfileHandler(
    private val plugin: EcoSpigotPlugin
) {
    private val handlerId = resolveHandlerId(plugin.configYml.getString("data-handler"))

    val defaultHandler = PersistentDataHandlers[handlerId]?.create(plugin)
        ?: throw IllegalArgumentException("Invalid data handler ($handlerId)")

    // Two Hikari pools over one sqlite file serialize against each other, so a server whose
    // configured handler is already sqlite shares the instance rather than opening a second pool.
    val localHandler = if (defaultHandler is SQLitePersistentDataHandler) {
        defaultHandler
    } else {
        sqliteHandlerFor(plugin)
    }

    val profileWriter = ProfileWriter(plugin, this)

    /**
     * The migration carrying profiles out of data.yml, or null when none is running.
     *
     * Read by the writer, which dual-writes for a profile being copied, and by the leaderboard
     * service, which ranks nobody until the sweep is done rather than ranking half a playerbase.
     */
    @Volatile
    var liveMigration: LiveProfileMigration? = null
        private set

    /**
     * Called once the live migration has carried every profile across, or null if nothing cares.
     */
    @Volatile
    var onLiveMigrationComplete: (() -> Unit)? = null

    /**
     * Writes into data.yml for a profile that is part-way through being carried out of it.
     */
    val dataYmlStore = DataYmlProfileStore(plugin.dataYml)

    private val loaded = ConcurrentHashMap<UUID, EcoProfile>()
    private val resolvedProfiles = ConcurrentHashMap<UUID, MutableSet<UUID>>()

    fun getPlayerProfile(uuid: UUID): EcoPlayerProfile {
        return loaded.computeIfAbsent(uuid) {
            EcoPlayerProfile(it, this)
        } as EcoPlayerProfile
    }

    fun getServerProfile(): EcoServerProfile {
        return loaded.computeIfAbsent(serverProfileUUID) {
            EcoServerProfile(this)
        } as EcoServerProfile
    }

    fun unloadProfile(uuid: UUID) {
        loaded.remove(uuid)
    }

    /**
     * Record that a player's data resolved to [profile], so it can be unloaded when they leave.
     *
     * A player can resolve to several profiles over a session, and only the last one is reachable
     * through the resolver by the time they quit - so the rest have to be remembered here or they
     * stay in [loaded] for the lifetime of the server.
     */
    fun trackResolvedProfile(player: UUID, profile: UUID) {
        if (player == profile) {
            return
        }

        resolvedProfiles.computeIfAbsent(player) { ConcurrentHashMap.newKeySet() }.add(profile)
    }

    /**
     * Unload a player's own profile along with every profile they resolved to.
     */
    fun unloadPlayer(player: UUID) {
        loaded.remove(player)
        resolvedProfiles.remove(player)?.forEach { loaded.remove(it) }
    }

    fun save() {
        for (handler in handlersToShutdown(localHandler, defaultHandler)) {
            handler.shutdown()
        }
    }

    fun migrateIfNecessary(): Boolean {
        // First install
        if (plugin.configYml.getBool("perform-data-migration") && !plugin.dataYml.has("previous-handler")) {
            plugin.dataYml.set("previous-handler", defaultHandler.id)
            plugin.dataYml.set(LEGACY_MIGRATED_KEY, true)
            plugin.dataYml.set(LOCAL_MIGRATED_KEY, true)
            plugin.dataYml.save()
            return false
        }

        val decision = decideMigration(
            migrationEnabled = plugin.configYml.getBool("perform-data-migration"),
            hasPreviousHandler = plugin.dataYml.has("previous-handler"),
            previousHandlerId = plugin.dataYml.getStringOrNull("previous-handler")?.lowercase(),
            defaultHandlerId = defaultHandler.id,
            defaultIsMySQL = defaultHandler is MySQLPersistentDataHandler,
            defaultIsMongoDB = defaultHandler is MongoDBPersistentDataHandler,
            legacyMigrated = plugin.dataYml.getBool(LEGACY_MIGRATED_KEY),
            localMigrated = plugin.dataYml.getBool(LOCAL_MIGRATED_KEY),
            hasStoredProfiles = plugin.dataYml.getSubsectionOrNull("player")
                ?.getKeys(false)?.isNotEmpty() == true
        )

        val fromFactory = when (decision.kind) {
            MigrationKind.NONE -> return false
            MigrationKind.LEGACY_MYSQL -> {
                plugin.logger.info("eco has detected a legacy MySQL database. Migrating to new MySQL database...")
                LegacyMySQLPersistentDataHandler.Factory
            }
            MigrationKind.LEGACY_MONGODB -> {
                plugin.logger.info("eco has detected a legacy MongoDB database. Migrating to new MongoDB database...")
                LegacyMongoDBPersistentDataHandler.Factory
            }
            MigrationKind.DEFAULT_HANDLER, MigrationKind.LOCAL_HANDLER ->
                factoryFor(decision.fromHandlerId) ?: return false
        }

        // data.yml is the one source that can be read while the server runs: it is a file eco owns
        // and nothing else writes to. Migrating out of it happens live, profile by profile, with no
        // lock and no restart. The legacy databases keep the locked copy.
        if (fromFactory == YamlPersistentDataHandler.Factory) {
            startLiveMigration(decision.kind)

            // The server runs normally either way: the migration needs the writer ticking to
            // dual-write, and a migration that could not start has touched nothing.
            return false
        }

        scheduleMigration(fromFactory, decision.kind)
        return true
    }

    /**
     * Carry profiles out of data.yml one at a time, while the server runs.
     *
     * A profile is copied the first time anything asks for it - on login, or when the background
     * sweep reaches it - and the target holding a row for a profile is what marks it as done. See
     * [LiveProfileMigration].
     *
     */
    private fun startLiveMigration(kind: MigrationKind) {
        // No backup, no migration: the copy leaves data.yml intact, but it is the only copy of the
        // data until the sweep finishes, and a profile part-way across is written to in both.
        if (DataYmlBackup.backup(plugin.dataFolder) == null) {
            plugin.logger.severe("Could not back up data.yml, so the migration has been postponed.")
            plugin.logger.severe("Fix the file permissions and restart. No data has been touched.")
            return
        }

        plugin.logger.info("eco is migrating player data out of data.yml")
        plugin.logger.info("Players can join throughout: profiles are carried across as they are needed.")

        val source = YamlPersistentDataHandler.Factory.create(plugin)

        val migration = LiveProfileMigration(
            source,
            // Read per profile rather than captured, so a key registered by a plugin that enables
            // late is carried for every profile migrated after it registers.
            {
                KeyRegistry.getRegisteredKeys().let {
                    if (kind == MigrationKind.LOCAL_HANDLER) {
                        it.filterTo(mutableSetOf()) { key -> key.isSavedLocally }
                    } else {
                        it
                    }
                }
            },
            { if (it.isSavedLocally) localHandler else defaultHandler },
            plugin.logger::info
        )

        liveMigration = migration

        // Run after 5 ticks to allow plugins to load their data keys, then off the main thread:
        // the sweep is a pass over every profile the server has ever seen.
        plugin.scheduler.global().runLater(5) {
            plugin.scheduler.runAsync {
                migration.sweep()
                finishLiveMigration()
            }
        }
    }

    private fun finishLiveMigration() {
        liveMigration = null

        plugin.dataYml.set("previous-handler", defaultHandler.id)
        plugin.dataYml.set(LEGACY_MIGRATED_KEY, true)
        plugin.dataYml.set(LOCAL_MIGRATED_KEY, true)
        // Everything carried here is done with data.yml, so the backfill never sweeps it again.
        plugin.dataYml.set(BACKFILLED_KEYS_KEY, KeyRegistry.getRegisteredKeys().map { it.key.toString() })
        plugin.dataYml.save()

        plugin.logger.info("Player data migration complete. data.yml is no longer read from.")

        onLiveMigrationComplete?.invoke()
    }

    /**
     * Carry [uuid] out of data.yml if a migration is running and has not reached it yet.
     *
     * Blocks for the length of one profile copy, so callers on the main thread should expect it to
     * do nothing at all - which is the case for every profile once the sweep has finished.
     */
    fun ensureMigrated(uuid: UUID) {
        liveMigration?.ensureMigrated(uuid)
    }

    /**
     * Carry the data of any key data.yml still holds and the migration never took.
     *
     * The migration takes the keys that are registered at the moment it runs, and a key registers
     * when something first constructs it - so a plugin that enables late, or is installed after the
     * migration, leaves its data behind in data.yml. This picks such a key up the first boot it is
     * registered on, rather than losing it with the plugin that was missing on migration day.
     */
    fun backfillIfNecessary() {
        if (!plugin.configYml.getBool("perform-data-migration")) {
            return
        }

        // data.yml is only a source once the migration has left it behind; a server still holding
        // its profiles there has a migration coming instead.
        if (plugin.dataYml.getSubsectionOrNull("player")?.getKeys(false).isNullOrEmpty()) {
            return
        }

        // A live migration is already carrying whole profiles across, key by key as they are
        // registered; sweeping the same file at the same time would fight it for no gain.
        if (liveMigration != null) {
            return
        }

        // Run after 5 ticks to allow plugins to load their data keys, as the migration does, and
        // off the main thread because every key means a pass over data.yml and the database.
        plugin.scheduler.global().runLater(5) {
            plugin.scheduler.runAsync {
                doBackfill()
            }
        }
    }

    private fun doBackfill() {
        val alreadyBackfilled = plugin.dataYml.getStrings(BACKFILLED_KEYS_KEY).toSet()

        val keys = KeyRegistry.getRegisteredKeys()
            .filterNot { it.key.toString() in alreadyBackfilled }

        if (keys.isEmpty()) {
            return
        }

        val source = YamlPersistentDataHandler.Factory.create(plugin)

        val written = keys.groupBy { it.isSavedLocally }.entries.sumOf { (isLocal, group) ->
            backfillProfiles(
                source,
                if (isLocal) localHandler else defaultHandler,
                group.toSet(),
                plugin.logger::info
            )
        }

        // Recorded whether or not anything was written: a key with nothing left for it in data.yml
        // has nothing to gain from being swept again next boot.
        plugin.dataYml.set(BACKFILLED_KEYS_KEY, (alreadyBackfilled + keys.map { it.key.toString() }).toList())
        plugin.dataYml.save()

        if (written > 0) {
            plugin.logger.info("Backfilled $written values from data.yml for ${keys.size} keys")
        }
    }

    private fun factoryFor(handlerId: String?): PersistentDataHandlerFactory? = when (handlerId) {
        null -> null
        // yaml is no longer registered, so the migration reaches its read-only factory directly.
        "yaml" -> YamlPersistentDataHandler.Factory
        else -> PersistentDataHandlers[handlerId]
    }

    private fun scheduleMigration(fromFactory: PersistentDataHandlerFactory, kind: MigrationKind) {
        ServerLocking.lock("Migrating player data! Check console for more information.")

        // Run after 5 ticks to allow plugins to load their data keys
        plugin.scheduler.global().runLater(5) {
            doMigrate(fromFactory, kind)
        }
    }

    private fun doMigrate(fromFactory: PersistentDataHandlerFactory, kind: MigrationKind) {
        // No backup, no migration: this is the one operation that rewrites every profile at once,
        // and the server stays locked rather than proceeding without a way back.
        if (DataYmlBackup.backup(plugin.dataFolder) == null) {
            plugin.logger.severe("Could not back up data.yml, so the migration has been aborted.")
            plugin.logger.severe("The server will stay locked. Fix the file permissions and restart.")
            return
        }

        // The local handler is the target only when the configured handler is already migrated and
        // the local keys are what is left behind in data.yml.
        val toHandler = if (kind == MigrationKind.LOCAL_HANDLER) localHandler else defaultHandler

        plugin.logger.info("eco is migrating player data")
        plugin.logger.info("${fromFactory.id} --> ${toHandler.id}")
        plugin.logger.info("This will take a while! Players will not be able to join during this time.")

        val keys = KeyRegistry.getRegisteredKeys().let {
            // A local migration carries only the keys that route locally; the rest already live in
            // the configured handler. Every other migration carries the whole registry, which on a
            // sqlite server is also how the local keys get carried.
            if (kind == MigrationKind.LOCAL_HANDLER) it.filterTo(mutableSetOf()) { key -> key.isSavedLocally } else it
        }

        migrateProfiles(fromFactory.create(plugin), toHandler, keys, plugin.logger::info)

        plugin.logger.info("Profile writes submitted! Waiting for completion...")
        toHandler.shutdown()

        plugin.logger.info("Updating previous handler...")
        plugin.dataYml.set("previous-handler", defaultHandler.id)
        plugin.dataYml.set(LEGACY_MIGRATED_KEY, true)
        // A default-handler migration on a sqlite server carried the local keys in the same pass,
        // so the local flag is set alongside it rather than firing a second migration next boot.
        plugin.dataYml.set(LOCAL_MIGRATED_KEY, true)
        // Everything carried here is done with data.yml, so the backfill never sweeps it again.
        plugin.dataYml.set(BACKFILLED_KEYS_KEY, keys.map { it.key.toString() })
        plugin.dataYml.save()
        plugin.logger.info("The server will now automatically be restarted...")

        plugin.server.shutdown()
    }
}
