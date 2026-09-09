package com.willfp.eco.internal.spigot.data.handlers.impl

// MySQL caps a prepared statement at 65535 placeholders. A single-key read spends that budget on
// uuids alone, but a batched read emits two placeholder lists -- one for uuids, one for keys -- so
// both have to fit inside one statement. Kept well below the hard cap: the margin covers the
// driver's own bookkeeping and leaves room for the predicate to grow without silently
// reintroducing the cap as a runtime failure on somebody's server.
const val MYSQL_PLACEHOLDER_BUDGET = 60_000

// SQLITE_MAX_VARIABLE_NUMBER is 32766 on 3.32 and later, and 999 on older builds, so the MySQL
// budget would fail outright here. Halved from the limit rather than shaved, because the number a
// server actually enforces depends on the sqlite build the driver was compiled against.
const val SQLITE_PLACEHOLDER_BUDGET = 30_000

// The uuid chunk that a single-key read has always used, retained as the ceiling so the common
// case -- a handful of ranked keys -- emits exactly the same uuid chunking as before this change.
const val UUID_CHUNK_SIZE = 1000

/**
 * Chunk sizes for a batched read of [keyCount] keys against a handler whose statement can carry
 * [budget] placeholders, as (uuids per statement, keys per statement).
 *
 * Keys take priority over uuids. Splitting the key list multiplies the number of statements just
 * as splitting the uuid list does, but the key list is bounded by how many keys are registered on
 * the server, while the uuid list is bounded by the size of the playerbase -- so the keys are the
 * cheaper list to keep whole.
 */
fun chunkSizesFor(keyCount: Int, budget: Int): Pair<Int, Int> {
    val keyChunk = keyCount.coerceIn(1, budget - 1)
    val uuidChunk = (budget - keyChunk).coerceIn(1, UUID_CHUNK_SIZE)

    return uuidChunk to keyChunk
}
