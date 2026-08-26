package com.willfp.eco.internal.spigot.datapack

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.LifecyclePosition
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.datapack.DatapackContributor
import com.willfp.eco.core.datapack.DatapackHandle
import com.willfp.eco.internal.spigot.proxies.DatapackCodecProxy
import org.bukkit.Bukkit
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer
import java.util.logging.Logger

/**
 * Owns every datapack handle, every registered contributor, and all the timing decisions.
 *
 * Consumers never call the layer on a schedule: they register once, and eco decides when the pack
 * needs rebuilding. Bootstrap-only content is generated from config an admin can edit at runtime,
 * so contributors are re-run on the owning plugin's reload as well as at registration. Without
 * that, every consumer would need its own reload hook and its own call into the layer, which is
 * exactly the duplicated logic this layer exists to remove.
 */
class DatapackRegistry(
    private val logger: Logger,
    private val dataYml: Config,
    private val saveData: () -> Unit,
    private val proxyProvider: () -> DatapackCodecProxy?,
    private val datapacksDir: () -> File = DatapackLocations::datapacksDir
) {
    private val ledger = CommitLedger(ConfigLedgerStorage(dataYml, LEDGER_PATH, saveData))

    private val restartCoordinator = RestartCoordinator(logger)

    private val validator by lazy { DatapackValidator(runCatching(proxyProvider).getOrNull(), logger) }

    private val packFormat by lazy { PackMcmeta.currentFormat(logger) }

    private val handles = ConcurrentHashMap<String, EcoDatapackHandle>()

    private val contributors = ConcurrentHashMap<String, CopyOnWriteArrayList<DatapackContributor>>()

    private val pendingCommits = ConcurrentHashMap<String, MutableList<DatapackEntry>>()

    @Volatile
    private var worldsLoaded = false

    /** Whether any plugin is waiting on a restart for its content to register. */
    val restartPending: Boolean
        get() = restartCoordinator.restartPending

    /**
     * The handle belonging to a plugin, created on first use.
     */
    fun handle(plugin: EcoPlugin): DatapackHandle = handles.computeIfAbsent(idOf(plugin)) { id ->
        EcoDatapackHandle(
            pluginId = id,
            displayName = plugin.name,
            packDir = File(datapacksDir(), DatapackLocations.packName(id)),
            validator = validator,
            ledger = ledger,
            restartCoordinator = restartCoordinator,
            logger = logger,
            packFormat = { packFormat },
            onPublished = { entries -> recordPublished(id, entries) }
        )
    }

    /**
     * Register a contributor, and rebuild the plugin's pack now.
     */
    fun register(plugin: EcoPlugin, contributor: DatapackContributor) {
        val id = idOf(plugin)
        val existing = contributors[id]

        if (existing == null) {
            contributors[id] = CopyOnWriteArrayList(listOf(contributor))

            // Re-emit on reload: an admin editing config at runtime must be able to get a pack that
            // is correct for the next restart, even though the change cannot apply now.
            plugin.onReload(LifecyclePosition.END) { rebuild(plugin) }
        } else {
            existing.add(contributor)
        }

        rebuild(plugin)
    }

    /**
     * Rebuild a plugin's pack from its registered contributors.
     */
    fun rebuild(plugin: EcoPlugin) {
        val registered = contributors[idOf(plugin)] ?: return

        // Explicit Consumer, because an `apply { }` block here would resolve to kotlin.apply.
        handle(plugin).apply(Consumer { draft ->
            for (contributor in registered) {
                runCatching { contributor.contribute(draft) }.onFailure {
                    logger.warning("[${plugin.name}] datapack contributor threw: ${it.message}")
                }
            }
        })

        // Rebuilds after startup (a plugin reload) get their own batched warning, since the one
        // printed at boot has already been and gone.
        if (worldsLoaded) {
            restartCoordinator.announce()
        }
    }

    /**
     * Called once the server's worlds are loaded.
     *
     * Everything published before this point has now been part of an enabled pack on a loaded
     * world, so it is committed, and the batched restart warning is worth printing.
     */
    fun onWorldsLoaded() {
        worldsLoaded = true

        for ((id, entries) in pendingCommits) {
            ledger.commit(id, entries)
        }

        pendingCommits.clear()

        restartCoordinator.announce()
    }

    /**
     * Deal with packs belonging to plugins that are no longer installed.
     *
     * @param remove Whether to delete them. Off by default, because it can break existing worlds.
     */
    fun handleOrphanedPacks(remove: Boolean) {
        val orphans = findOrphans()

        if (orphans.isEmpty()) {
            return
        }

        for (orphan in orphans) {
            val id = orphan.name.removePrefix(PACK_PREFIX)

            if (!remove) {
                logger.warning(
                    "Datapack ${orphan.name} belongs to a plugin that is no longer installed. " +
                            "Set datapacks.remove-orphaned to true in eco's config.yml to delete it " +
                            "(this can break worlds generated with its content)."
                )
                continue
            }

            when (val outcome = PackPublisher(orphan).delete()) {
                is PublishOutcome.Failed ->
                    outcome.messages.forEach { logger.warning("Could not remove ${orphan.name}: $it") }

                else -> {
                    ledger.release(id)
                    logger.warning("Removed orphaned datapack ${orphan.name}")
                }
            }
        }
    }

    private fun findOrphans(): List<File> {
        val dir = runCatching(datapacksDir).getOrNull() ?: return emptyList()

        if (!dir.isDirectory) {
            return emptyList()
        }

        val installed = runCatching {
            Bukkit.getPluginManager().plugins.map { it.name.lowercase() }.toSet()
        }.getOrElse { return emptyList() }

        return dir.listFiles().orEmpty()
            .filter { it.isDirectory && it.name.startsWith(PACK_PREFIX) }
            .filter { it.name.removePrefix(PACK_PREFIX) !in installed }
    }

    private fun recordPublished(id: String, entries: List<DatapackEntry>) {
        if (worldsLoaded) {
            ledger.commit(id, entries)
            return
        }

        pendingCommits.computeIfAbsent(id) { mutableListOf() }.addAll(entries)
    }

    private fun idOf(plugin: EcoPlugin) = plugin.name.lowercase()

    private companion object {
        const val LEDGER_PATH = "datapacks"
        const val PACK_PREFIX = "eco_"
    }
}
