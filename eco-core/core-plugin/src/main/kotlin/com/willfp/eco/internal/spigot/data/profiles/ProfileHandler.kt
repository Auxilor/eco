package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.ServerLocking
import com.willfp.eco.internal.spigot.data.Bookkeeping
import com.willfp.eco.internal.spigot.data.KeyRegistry
import com.willfp.eco.internal.spigot.data.getBool
import com.willfp.eco.internal.spigot.data.has
import com.willfp.eco.internal.spigot.data.setBool
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
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.Configs
import com.willfp.eco.core.data.handlers.PersistentDataHandler
import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

const val LEGACY_MIGRATED_KEY = "legacy-data-migrated"
const val LOCAL_MIGRATED_KEY = "local-handler-migrated"

// The handler whose store the profiles were last in, and so what a boot migrates out of.
const val PREVIOUS_HANDLER_KEY = "previous-handler"

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

    /**
     * eco's own bookkeeping, kept in the local database rather than in data.yml.
     *
     * On the local handler because it is the one store every server has, whatever it has
     * configured: a server on MySQL still keeps its own migration flags locally, where they
     * belong, rather than in a database it may share with other servers.
     */
    val bookkeeping: Bookkeeping = localHandler.bookkeeping

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
    val dataYmlStore by lazy { DataYmlProfileStore(plugin.dataYml) }

    /**
     * data.yml, which exists only until a migration has finished with it.
     *
     * Reached through this rather than through the plugin directly, because the config recreates
     * the file from the jar the moment it is constructed -- so a server that has retired data.yml
     * would grow an empty one back on the next boot that so much as looked at it.
     */
    private val dataYmlFile = File(plugin.dataFolder, "data.yml")

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
        // Before anything reads a flag: on the first boot after the store moved, the flags are
        // still in data.yml and the bookkeeping is empty.
        importDataYmlBookkeeping()

        // First install
        if (plugin.configYml.getBool("perform-data-migration") && !bookkeeping.has(PREVIOUS_HANDLER_KEY)) {
            bookkeeping.set(PREVIOUS_HANDLER_KEY, defaultHandler.id)
            bookkeeping.setBool(LEGACY_MIGRATED_KEY, true)
            bookkeeping.setBool(LOCAL_MIGRATED_KEY, true)
            return false
        }

        val decision = decideMigration(
            migrationEnabled = plugin.configYml.getBool("perform-data-migration"),
            hasPreviousHandler = bookkeeping.has(PREVIOUS_HANDLER_KEY),
            previousHandlerId = bookkeeping.get(PREVIOUS_HANDLER_KEY)?.lowercase(),
            defaultHandlerId = defaultHandler.id,
            defaultIsMySQL = defaultHandler is MySQLPersistentDataHandler,
            defaultIsMongoDB = defaultHandler is MongoDBPersistentDataHandler,
            legacyMigrated = bookkeeping.getBool(LEGACY_MIGRATED_KEY),
            localMigrated = bookkeeping.getBool(LOCAL_MIGRATED_KEY),
            hasStoredProfiles = dataYmlFile.isFile && holdsProfiles(plugin.dataYml)
        )

        val fromFactory = when (decision.kind) {
            MigrationKind.NONE -> {
                // Nothing to carry, but a server migrated by a version that left the profiles in
                // data.yml is still carrying the whole file into every reload.
                retireIfNecessary()
                return false
            }
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
        val backup = DataYmlBackup.backup(plugin.dataFolder)

        if (backup == null) {
            plugin.logger.severe("Could not back up data.yml, so the migration has been postponed.")
            plugin.logger.severe("Fix the file permissions and restart. No data has been touched.")
            return
        }

        plugin.logger.info("eco is migrating player data out of data.yml")
        plugin.logger.info("Players can join throughout: profiles are carried across as they are needed.")

        val source = YamlPersistentDataHandler.Factory.create(plugin)

        // The sweep runs off the main thread and nothing waits on it, so it is free to take its
        // time rather than spend the whole migration hammering the database players are using.
        val throttleMillis = plugin.configYml.getInt("migration-throttle-ms").toLong()
        val throttle: () -> Unit = if (throttleMillis > 0) {
            { Thread.sleep(throttleMillis) }
        } else {
            { }
        }

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
            plugin.logger::info,
            throttle
        )

        liveMigration = migration

        // Run after 5 ticks to allow plugins to load their data keys, then off the main thread:
        // the sweep is a pass over every profile the server has ever seen.
        plugin.scheduler.global().runLater(5) {
            plugin.scheduler.runAsync {
                migration.sweep()

                // Before the flags that end the migration are written: once data.yml is marked
                // done, nothing reads it again, so anything a half-finished copy left in it has
                // to be carried now or not at all.
                val repaired = migration.verify()

                if (repaired > 0) {
                    plugin.logger.info("Carried across $repaired values the migration had missed")
                }

                finishLiveMigration(backup.name)
            }
        }
    }

    private fun finishLiveMigration(backupName: String) {
        liveMigration = null

        bookkeeping.set(PREVIOUS_HANDLER_KEY, defaultHandler.id)
        bookkeeping.setBool(LEGACY_MIGRATED_KEY, true)
        bookkeeping.setBool(LOCAL_MIGRATED_KEY, true)
        // Everything carried here is done with data.yml, so the backfill never sweeps it again.
        bookkeeping.setList(BACKFILLED_KEYS_KEY, KeyRegistry.getRegisteredKeys().map { it.key.toString() })
        // The profiles live in the database now, and the backup is what the backfill reads.
        retireDataYmlProfiles(bookkeeping, backupName)

        // Nothing reads data.yml from here on, and nothing writes it either: every value it held
        // is in the database above, and the profiles are in $backupName.
        deleteDataYml(plugin.dataFolder)

        plugin.logger.info("Player data migration complete. data.yml has been removed; $backupName keeps a copy.")

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

        // No source, nothing to backfill: a server whose migration predates the backup being
        // recorded reads data.yml itself, and one that never had profiles in yaml reads nothing.
        if (!hasBackfillSource()) {
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
        val alreadyBackfilled = bookkeeping.getList(BACKFILLED_KEYS_KEY).toSet()

        val keys = KeyRegistry.getRegisteredKeys()
            .filterNot { it.key.toString() in alreadyBackfilled }

        if (keys.isEmpty()) {
            return
        }

        // Loaded here rather than on the main thread: the backup is the whole playerbase as yaml,
        // and parsing it is the reason the profiles were taken out of data.yml in the first place.
        val source = backfillSource() ?: return

        // Runs off the main thread with nothing waiting on it, as the migration's sweep does, so
        // it is free to take its time rather than hammer the database players are using.
        val throttleMillis = plugin.configYml.getInt("migration-throttle-ms").toLong()
        val throttle: () -> Unit = if (throttleMillis > 0) {
            { Thread.sleep(throttleMillis) }
        } else {
            { }
        }

        val written = keys.groupBy { it.isSavedLocally }.entries.sumOf { (isLocal, group) ->
            backfillProfiles(
                source,
                if (isLocal) localHandler else defaultHandler,
                group.toSet(),
                plugin.logger::info,
                pause = throttle
            )
        }

        // Recorded whether or not anything was written: a key with nothing left for it in data.yml
        // has nothing to gain from being swept again next boot.
        bookkeeping.setList(BACKFILLED_KEYS_KEY, (alreadyBackfilled + keys.map { it.key.toString() }).toList())

        if (written > 0) {
            plugin.logger.info("Backfilled $written values from data.yml for ${keys.size} keys")
        }
    }

    /**
     * Carry eco's own bookkeeping out of data.yml, on the first boot after the store moved.
     *
     * Reading data.yml is what constructs it, and constructing it is what recreates the file, so
     * this is skipped entirely once the file is gone -- which is the state every server ends up in.
     */
    private fun importDataYmlBookkeeping() {
        if (!dataYmlFile.isFile) {
            return
        }

        if (importBookkeeping(plugin.dataYml, bookkeeping)) {
            plugin.logger.info("Moved eco's bookkeeping out of data.yml and into ${localHandler.id}")
        }
    }

    /**
     * Retire a data.yml the migration has already finished with.
     *
     * A server migrated by a version that left the profiles there loads, walks and rewrites the
     * whole playerbase as yaml on every boot, reload and autosave -- for data nothing reads. This
     * is the one-off that ends that: the profiles are backed up as a migration backs them up, and
     * then the file goes, because everything else it held now lives in [bookkeeping].
     */
    private fun retireIfNecessary() {
        if (!dataYmlFile.isFile) {
            return
        }

        // Only a data.yml the migration is done with. Anything else is still the live store, and
        // a migration that has not run yet is what reads it.
        val migrated = bookkeeping.get(PREVIOUS_HANDLER_KEY)?.lowercase() == defaultHandler.id &&
                bookkeeping.getBool(LEGACY_MIGRATED_KEY) &&
                bookkeeping.getBool(LOCAL_MIGRATED_KEY)

        if (!migrated) {
            return
        }

        if (holdsProfiles(plugin.dataYml)) {
            // The migration's own backup already holds these profiles, so a second copy is only
            // taken when the recorded one is gone -- or was never recorded, which is the case this
            // exists for.
            val backup = recordedBackfillSource() ?: DataYmlBackup.backup(plugin.dataFolder)

            if (backup == null) {
                plugin.logger.severe("Could not back up data.yml, so it was left in place.")
                return
            }

            retireDataYmlProfiles(bookkeeping, backup.name)
        }

        if (deleteDataYml(plugin.dataFolder)) {
            plugin.logger.info("Removed data.yml. eco keeps its data in ${localHandler.id} now.")
        }
    }

    /**
     * The backup recorded as holding the profiles data.yml used to, or null if there is none.
     */
    private fun recordedBackfillSource(): File? =
        bookkeeping.get(BACKFILL_SOURCE_KEY)
            ?.let { File(plugin.dataFolder, it) }
            ?.takeIf { it.isFile }

    /**
     * Whether there is anything for the backfill to read, without reading it.
     */
    private fun hasBackfillSource(): Boolean =
        recordedBackfillSource() != null || (dataYmlFile.isFile && holdsProfiles(plugin.dataYml))

    /**
     * The store the backfill reads a late-registered key's data out of.
     *
     * The migration's backup, once there is one: data.yml keeps only eco's own bookkeeping after a
     * migration, and the backup is the copy of the playerbase it took before it ran. A server that
     * migrated before backups were recorded still has the profiles in data.yml, and reads those.
     */
    private fun backfillSource(): PersistentDataHandler? {
        val recorded = recordedBackfillSource()

        if (recorded == null) {
            return if (dataYmlFile.isFile && holdsProfiles(plugin.dataYml)) {
                YamlPersistentDataHandler(plugin.dataYml)
            } else {
                null
            }
        }

        // The type is passed rather than inferred: the backup's extension is .bak, and the
        // inferring overload answers with an empty config for an extension it does not know.
        return YamlPersistentDataHandler(Configs.fromFile(recorded, ConfigType.YAML))
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
        //
        // Only for a migration that reads data.yml, though. Every other source has its own store,
        // and on a server that has already retired data.yml there is no file to copy -- so
        // demanding one would lock a server that had nothing at risk.
        val readsDataYml = fromFactory == YamlPersistentDataHandler.Factory
        val backup = if (readsDataYml) DataYmlBackup.backup(plugin.dataFolder) else null

        if (readsDataYml && backup == null) {
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
        bookkeeping.set(PREVIOUS_HANDLER_KEY, defaultHandler.id)
        bookkeeping.setBool(LEGACY_MIGRATED_KEY, true)
        // A default-handler migration on a sqlite server carried the local keys in the same pass,
        // so the local flag is set alongside it rather than firing a second migration next boot.
        bookkeeping.setBool(LOCAL_MIGRATED_KEY, true)
        // Everything carried here is done with data.yml, so the backfill never sweeps it again.
        bookkeeping.setList(BACKFILLED_KEYS_KEY, keys.map { it.key.toString() })

        // Only for a migration that read data.yml: another handler's profiles were never in it.
        if (backup != null) {
            retireDataYmlProfiles(bookkeeping, backup.name)

            // The bookkeeping lives in the database now, so the file has nothing left to hold.
            deleteDataYml(plugin.dataFolder)
        }

        plugin.logger.info("The server will now automatically be restarted...")

        plugin.server.shutdown()
    }
}
