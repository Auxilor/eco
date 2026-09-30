package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.internal.spigot.data.Bookkeeping
import com.willfp.eco.internal.spigot.datapack.LEDGER_PREFIX
import java.io.File

// The backup that holds the profiles data.yml used to, and the backfill's source from then on.
const val BACKFILL_SOURCE_KEY = "backfill-source"

// Where the yaml handler reads profiles from, and the one section a migrated data.yml drops.
const val PLAYER_SECTION = "player"

// The section the datapack ledger used to live in, before it moved into the bookkeeping store.
private const val LEDGER_SECTION = "datapacks"

// Every value data.yml held that was eco's own bookkeeping rather than profile data.
private val SINGLE_VALUE_KEYS = listOf(
    "previous-handler",
    LEGACY_MIGRATED_KEY,
    LOCAL_MIGRATED_KEY,
    BACKFILL_SOURCE_KEY
)

/**
 * Take the profile data out of data.yml once the migration has carried it across.
 *
 * data.yml is a config eco keeps loaded, updates on every reload, and rewrites in full on every
 * autosave. A migrated server's copy is dead weight in all three: tens of thousands of profiles
 * that nothing reads, walked on the main thread by the config updater at every /eco reload and
 * written back out whole every autosave -- which is also why deleting the file by hand does not
 * stick, as the next save writes the in-memory copy straight back.
 *
 * The profiles are not lost by dropping them here: [backupName] is the copy the migration took
 * before it ran, and it is recorded so the backfill can still reach a key that registers later.
 */
fun retireDataYmlProfiles(bookkeeping: Bookkeeping, backupName: String) {
    bookkeeping.set(BACKFILL_SOURCE_KEY, backupName)
}

/**
 * Whether data.yml still holds profiles of its own.
 */
fun holdsProfiles(dataYml: Config): Boolean =
    dataYml.getSubsectionOrNull(PLAYER_SECTION)?.getKeys(false)?.isNotEmpty() == true

/**
 * Carry eco's own bookkeeping out of data.yml and into [bookkeeping].
 *
 * This runs once, on the first boot after the store moved, and is what makes the file itself
 * disposable: everything in it that was not profile data ends up in the same database the profiles
 * did. A value the store already holds is left alone -- the store is the source of truth the
 * moment it has anything, and a stale data.yml must never overwrite it.
 *
 * Answers with whether anything was carried, so the caller can say so.
 */
fun importBookkeeping(dataYml: Config, bookkeeping: Bookkeeping): Boolean {
    var imported = false

    for (key in SINGLE_VALUE_KEYS) {
        val value = dataYml.getStringOrNull(key) ?: continue

        if (bookkeeping.get(key) != null) {
            continue
        }

        bookkeeping.set(key, value)
        imported = true
    }

    val backfilled = dataYml.getStrings(BACKFILLED_KEYS_KEY)

    if (backfilled.isNotEmpty() && bookkeeping.getList(BACKFILLED_KEYS_KEY).isEmpty()) {
        bookkeeping.setList(BACKFILLED_KEYS_KEY, backfilled)
        imported = true
    }

    val ledger = dataYml.getSubsectionOrNull(LEDGER_SECTION)

    if (ledger != null) {
        for (plugin in ledger.getKeys(false)) {
            val tokens = ledger.getStrings(plugin)

            if (tokens.isEmpty() || bookkeeping.getList(LEDGER_PREFIX + plugin).isNotEmpty()) {
                continue
            }

            bookkeeping.setList(LEDGER_PREFIX + plugin, tokens)
            imported = true
        }
    }

    return imported
}

/**
 * Delete data.yml, and answer with whether it was there to delete.
 *
 * Safe only once [importBookkeeping] has run and the profiles are in the database: the file is not
 * recreated, because nothing constructs the config again once eco stops reading it.
 */
fun deleteDataYml(dataFolder: File): Boolean {
    val file = File(dataFolder, "data.yml")

    return file.isFile && file.delete()
}
