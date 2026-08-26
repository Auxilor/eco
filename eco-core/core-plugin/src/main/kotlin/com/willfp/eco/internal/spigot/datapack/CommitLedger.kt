package com.willfp.eco.internal.spigot.datapack

import com.willfp.eco.core.config.interfaces.Config

/**
 * Where the ledger is persisted.
 */
interface LedgerStorage {
    /** Plugin ID to committed entry tokens. */
    fun read(): Map<String, Set<String>>

    fun write(data: Map<String, Set<String>>)
}

/**
 * In-memory storage, for tests and for servers with no writable data file.
 */
class MemoryLedgerStorage(initial: Map<String, Set<String>> = emptyMap()) : LedgerStorage {
    private var data: Map<String, Set<String>> = initial.mapValues { it.value.toSet() }

    override fun read() = data

    override fun write(data: Map<String, Set<String>>) {
        this.data = data.mapValues { it.value.toSet() }
    }
}

/**
 * Storage backed by a section of one of eco's configs.
 */
class ConfigLedgerStorage(
    private val config: Config,
    private val path: String,
    private val save: () -> Unit
) : LedgerStorage {
    override fun read(): Map<String, Set<String>> {
        val section = config.getSubsectionOrNull(path) ?: return emptyMap()

        return section.getKeys(false).associateWith { section.getStrings("$it").toSet() }
    }

    override fun write(data: Map<String, Set<String>>) {
        config.set(path, null)

        for ((plugin, tokens) in data) {
            config.set("$path.$plugin", tokens.sorted())
        }

        save()
    }
}

/**
 * Tracks which entries a loaded world has committed to.
 *
 * Removing a bootstrap-only entry that a world has already generated with corrupts that world:
 * chunks reference a biome or dimension ID that no longer resolves. Detecting actual placement
 * would mean scanning every chunk, which is impractical, so the ledger records the weaker fact that
 * an entry has been live on a world that has been loaded.
 *
 * That is deliberately conservative. It will sometimes refuse to remove an entry that was never
 * placed. That is the right direction to be wrong in.
 */
class CommitLedger(
    private val storage: LedgerStorage
) {
    private val lock = Any()

    /**
     * Record that [entries] have been live on a loaded world.
     *
     * Reloadable entries are never committed: they can always be removed.
     */
    fun commit(pluginId: String, entries: Collection<DatapackEntry>) {
        val tokens = entries
            .filterNot { LifecycleClassifier.isReloadable(it.registry) }
            .map { token(it) }

        commitTokens(pluginId, tokens)
    }

    /**
     * Record raw tokens, as recovered from a pack already on disk.
     */
    fun commitTokens(pluginId: String, tokens: Collection<String>) {
        if (tokens.isEmpty()) {
            return
        }

        synchronized(lock) {
            val data = storage.read().toMutableMap()
            val existing = data[pluginId].orEmpty()
            val updated = existing + tokens

            if (updated == existing) {
                return
            }

            data[pluginId] = updated
            storage.write(data)
        }
    }

    /**
     * The entries committed by a plugin.
     */
    fun committed(pluginId: String): Set<String> = synchronized(lock) {
        storage.read()[pluginId].orEmpty()
    }

    /**
     * Which of [entries] are committed, and so cannot be removed.
     */
    fun blocking(pluginId: String, entries: Collection<DatapackEntry>): Set<String> {
        val committed = committed(pluginId)
        return entries.map { token(it) }.filter { it in committed }.toSet()
    }

    /**
     * Forget everything a plugin has committed.
     *
     * Only ever called behind an explicit, informed admin action.
     */
    fun release(pluginId: String) {
        synchronized(lock) {
            val data = storage.read().toMutableMap()

            if (data.remove(pluginId) != null) {
                storage.write(data)
            }
        }
    }

    /**
     * The plugin IDs with committed entries.
     */
    fun plugins(): Set<String> = synchronized(lock) {
        storage.read().keys.toSet()
    }

    companion object {
        /**
         * The stable identity of an entry, independent of file extension.
         */
        fun token(entry: DatapackEntry) =
            "${entry.registry.trim('/')}|${entry.id.namespace}:${entry.id.key}"
    }
}
