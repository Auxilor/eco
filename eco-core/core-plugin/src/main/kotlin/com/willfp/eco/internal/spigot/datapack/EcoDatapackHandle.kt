package com.willfp.eco.internal.spigot.datapack

import com.willfp.eco.core.datapack.DatapackDraft
import com.willfp.eco.core.datapack.DatapackHandle
import com.willfp.eco.core.datapack.InstallResult
import java.io.File
import java.util.function.Consumer
import java.util.logging.Logger

/**
 * One plugin's pack.
 *
 * @param pluginId    The owning plugin's ID.
 * @param displayName The owning plugin's name, used in log output and the pack description.
 */
class EcoDatapackHandle(
    private val pluginId: String,
    private val displayName: String,
    private val packDir: File,
    private val validator: DatapackValidator,
    private val ledger: CommitLedger,
    private val restartCoordinator: RestartCoordinator,
    private val logger: Logger,
    private val packFormat: () -> PackFormat?,
    private val onPublished: (List<DatapackEntry>) -> Unit
) : DatapackHandle {
    private val publisher = PackPublisher(packDir)

    override fun apply(builder: Consumer<DatapackDraft>): InstallResult {
        val draft = EcoDatapackDraft()
        builder.accept(draft)

        val entries = draft.entries

        collectErrors(entries)
            .takeIf { it.isNotEmpty() }
            ?.let { return failed(it) }

        val files = buildFiles(entries)

        return when (val outcome = publisher.publish(files)) {
            is PublishOutcome.Failed -> failed(outcome.messages)

            is PublishOutcome.Unchanged -> {
                // Deliberately does not mark a restart pending. Identical content is either already
                // live from a previous boot, or was written earlier this boot by the call that did
                // mark it. Marking here would prompt for a restart on every boot, forever.
                onPublished(entries)
                InstallResult(InstallResult.Status.UNCHANGED)
            }

            is PublishOutcome.Written -> {
                onPublished(entries)
                warnDropped(entries)
                published(entries)
            }
        }
    }

    override fun remove(): InstallResult {
        warnCommitted()

        val result = doRemove()

        if (result.succeeded()) {
            ledger.release(pluginId)
        }

        return result
    }

    override fun restartPending() = restartCoordinator.isPending(pluginId)

    private fun doRemove(): InstallResult = when (val outcome = publisher.delete()) {
        is PublishOutcome.Failed -> failed(outcome.messages)

        is PublishOutcome.Unchanged -> InstallResult(InstallResult.Status.UNCHANGED)

        is PublishOutcome.Written -> {
            logger.info("[$displayName] datapack removed")
            InstallResult(InstallResult.Status.READY)
        }
    }

    /**
     * Warn about committed entries this publish dropped, and stop tracking them.
     *
     * An admin editing config has to be able to shrink a pack, so a rebuild never refuses. Dropping
     * bootstrap-only content a loaded world has seen still leaves chunks and entities referencing
     * IDs that no longer resolve, which is worth saying out loud. This is the path that actually
     * happens: a feature toggled off, or an entry deleted from config.
     */
    private fun warnDropped(entries: List<DatapackEntry>) {
        val current = entries.map { CommitLedger.token(it) }.toSet()
        val dropped = ledger.committed(pluginId) - current

        if (dropped.isEmpty()) {
            return
        }

        logger.warning(
            "[$displayName] datapack rebuild dropped ${dropped.size} entries that have been live on " +
                    "a loaded world: ${dropped.sorted().joinToString(", ")}"
        )

        warnOrphanedIds()

        ledger.releaseTokens(pluginId, dropped)
    }

    private fun warnCommitted() {
        val committed = ledger.committed(pluginId)

        if (committed.isEmpty()) {
            return
        }

        logger.warning(
            "[$displayName] removing a datapack holding ${committed.size} entries that have been " +
                    "live on a loaded world: ${committed.sorted().joinToString(", ")}"
        )

        warnOrphanedIds()
    }

    private fun warnOrphanedIds() {
        logger.warning(
            "[$displayName] anything generated with them references IDs that will no longer resolve, " +
                    "and may fail to load after the next restart."
        )
    }

    private fun buildFiles(entries: List<DatapackEntry>): Map<String, ByteArray> {
        val files = LinkedHashMap<String, ByteArray>()

        files["pack.mcmeta"] = PackMcmeta
            .generate("$displayName (eco)", packFormat())
            .toByteArray(Charsets.UTF_8)

        for (entry in entries) {
            files[entry.path] = if (entry.isJson) {
                JsonCanonicaliser.canonicalise(entry.content.toString(Charsets.UTF_8))
                    .toByteArray(Charsets.UTF_8)
            } else {
                entry.content
            }
        }

        return files
    }

    private fun collectErrors(entries: List<DatapackEntry>): List<String> {
        val errors = mutableListOf<String>()
        val seen = mutableSetOf<String>()

        for (entry in entries) {
            if (!seen.add(entry.path)) {
                errors.add("$entry: duplicate entry, two drafts wrote ${entry.path}")
                continue
            }

            validator.validate(entry)?.let { errors.add(it) }
        }

        return errors
    }

    private fun published(entries: List<DatapackEntry>): InstallResult {
        val restartRequired = markRestartIfNeeded(entries)
        val count = entries.size

        return if (restartRequired) {
            logger.info(
                "[$displayName] datapack updated ($count entries); restart required to register the changes"
            )
            InstallResult(
                InstallResult.Status.RESTART_REQUIRED,
                listOf("$count entries written; a server restart is required for them to take effect.")
            )
        } else {
            logger.info("[$displayName] datapack updated ($count entries)")
            InstallResult(
                InstallResult.Status.READY,
                listOf("$count entries written; they apply on the next datapack reload.")
            )
        }
    }

    private fun markRestartIfNeeded(entries: List<DatapackEntry>): Boolean {
        // Eco writes during its load phase, which is after datapack discovery, so bootstrap-only
        // content can never take effect on the boot that wrote it. This is uniform across Spigot
        // and Paper: eco is a legacy plugin.yml plugin with no bootstrapper.
        val restartRequired = LifecycleClassifier.requiresRestart(entries.map { it.registry })

        if (restartRequired) {
            restartCoordinator.markPending(pluginId)
        }

        return restartRequired
    }

    private fun failed(messages: List<String>): InstallResult {
        for (message in messages) {
            logger.warning("[$displayName] $message")
        }

        return InstallResult(InstallResult.Status.FAILED, messages)
    }
}
