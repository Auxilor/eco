package com.willfp.eco.internal.spigot.datapack

import com.willfp.eco.internal.spigot.data.Bookkeeping

/**
 * The prefix every ledger entry is stored under, so the ledger can be read back without keeping a
 * separate index of which plugins have committed.
 */
const val LEDGER_PREFIX = "datapacks/"

/**
 * Ledger storage backed by eco's [Bookkeeping] rather than by a config file.
 *
 * One entry per plugin, holding that plugin's committed tokens. The ledger is the last thing that
 * kept data.yml alive on a migrated server; storing it here is what lets that file go.
 */
class BookkeepingLedgerStorage(
    private val bookkeeping: Bookkeeping
) : LedgerStorage {
    override fun read(): Map<String, Set<String>> =
        bookkeeping.keysStartingWith(LEDGER_PREFIX).associate {
            it.removePrefix(LEDGER_PREFIX) to bookkeeping.getList(it).toSet()
        }

    override fun write(data: Map<String, Set<String>>) {
        // Cleared by difference rather than wholesale: a plugin whose entry is unchanged should
        // not have its rows deleted and reinserted every time another plugin commits.
        val stale = bookkeeping.keysStartingWith(LEDGER_PREFIX)
            .filterNot { it.removePrefix(LEDGER_PREFIX) in data }

        for (key in stale) {
            bookkeeping.setList(key, emptyList())
        }

        for ((plugin, tokens) in data) {
            bookkeeping.setList(LEDGER_PREFIX + plugin, tokens.sorted())
        }
    }
}
