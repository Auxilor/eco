@file:JvmName("LeaderboardPlaceholders")

package com.willfp.eco.core.leaderboard

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.placeholder.DynamicPlaceholder
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.core.placeholder.PlayerlessPlaceholder
import java.math.BigDecimal
import java.util.regex.Pattern

/**
 * Render a standing as display text: `#3` for an exact rank, `Top 12.5%` for a percentile band,
 * and [emptyText] for an unranked player.
 *
 * Trailing zeroes are stripped from the percentile, so a whole percentage carries no decimal
 * part: `Top 5%`, not `Top 5.0%`.
 *
 * @param emptyText The text to use for an unranked player.
 * @return The display text.
 */
fun LeaderboardRank.toDisplayString(emptyText: String): String {
    val rank = this.rank
    if (rank != null) {
        return "#$rank"
    }

    val percent = this.percent
    if (percent != null) {
        return "Top ${BigDecimal.valueOf(percent).stripTrailingZeros().toPlainString()}%"
    }

    return emptyText
}

/**
 * Read the position back out of the args a positional placeholder matched, using the same
 * compiled pattern the placeholder was registered with.
 */
private fun positionIn(pattern: Pattern, args: String): Int? {
    val matcher = pattern.matcher(args)

    if (!matcher.matches()) {
        return null
    }

    return matcher.group(1).toIntOrNull()
}

/**
 * Register the standard placeholder set for a leaderboard, so that every plugin exposes its
 * leaderboards under the same names with the same behaviour.
 *
 * For a leaderboard registered with a [prefix] of `mining` in the plugin `ecoskills`:
 *
 * - `%ecoskills_mining_rank%` - the player's exact position, or [emptyText] if unranked.
 * - `%ecoskills_mining_rank_display%` - the player's standing as `#3` or `Top 12.5%`, honouring
 *   the server's `exact-rank-cutoff`, or [emptyText] if unranked.
 * - `%ecoskills_mining_tracked%` - the amount of players the last snapshot ranked.
 * - `%ecoskills_mining_top_<N>_name%` - the name of the player at position N, or [emptyText].
 * - `%ecoskills_mining_top_<N>_value%` - the value at position N through [formatValue], or
 *   [emptyText].
 *
 * `%..._rank%` is always exact and never a percentile: it reads [Leaderboard.getPosition] rather
 * than [Leaderboard.getRank], so a plugin that never had percentiles keeps the placeholder its
 * users already have. `%..._rank_display%` is the one that honours the cutoff.
 *
 * The two positional placeholders are single regex-matched placeholders rather than one
 * registration per position, so an uncapped `max-entries` works for any N without registering
 * thousands of placeholders.
 *
 * Registration is idempotent per plugin **only** if the caller calls
 * [Leaderboards.unregisterAll] and re-registers in its reload handler: this helper never
 * unregisters anything itself. Re-registering the same prefix for the same plugin replaces the
 * previous placeholders in place (they are keyed by pattern), but a prefix that was renamed or
 * removed from the config has no replacement coming, and its placeholders would be left behind
 * reading a leaderboard that is no longer refreshed.
 *
 * @param plugin      The plugin that owns the placeholders.
 * @param prefix      The placeholder prefix, usually the leaderboard's unqualified ID.
 * @param emptyText   The text to use where there is no value: an unranked player, or a position
 *                    beyond the retained entries.
 * @param formatValue Formats a leaderboard value for display.
 */
fun Leaderboard.registerStandardPlaceholders(
    plugin: EcoPlugin,
    prefix: String,
    emptyText: String,
    formatValue: (Double) -> String
) {
    val leaderboard = this

    PlayerPlaceholder(plugin, "${prefix}_rank") { player ->
        leaderboard.getPosition(player.uniqueId)?.toString() ?: emptyText
    }.register()

    PlayerPlaceholder(plugin, "${prefix}_rank_display") { player ->
        leaderboard.getRank(player.uniqueId).toDisplayString(emptyText)
    }.register()

    PlayerlessPlaceholder(plugin, "${prefix}_tracked") {
        leaderboard.snapshot.trackedPlayers.toString()
    }.register()

    val quoted = Pattern.quote(prefix)

    // The position is read back out by re-matching the very pattern the placeholder was
    // registered with, so there is only ever one parser for these args.
    val namePattern = Pattern.compile("${quoted}_top_(\\d+)_name")
    val valuePattern = Pattern.compile("${quoted}_top_(\\d+)_value")

    DynamicPlaceholder(plugin, namePattern) { args ->
        val position = positionIn(namePattern, args) ?: return@DynamicPlaceholder emptyText

        // getPlayer resolves an OfflinePlayer, so it is only touched here, where the name is
        // actually needed. A player the server has never seen has no name.
        leaderboard.getTop(position)?.player?.name ?: emptyText
    }.register()

    DynamicPlaceholder(plugin, valuePattern) { args ->
        val position = positionIn(valuePattern, args) ?: return@DynamicPlaceholder emptyText
        val entry = leaderboard.getTop(position) ?: return@DynamicPlaceholder emptyText

        formatValue(entry.value)
    }.register()
}

/**
 * Register the standard placeholder set for a playerbase tally, exposing every bucket as
 * `%<plugin>_<prefix>_<bucket>_count%`.
 *
 * For a tally registered with a [prefix] of `jobs` in the plugin `ecojobs`,
 * `%ecojobs_jobs_miner_count%` resolves to the amount of players in the `miner` bucket. Buckets
 * the last refresh did not produce resolve to [emptyText] rather than to zero, so a typo in a
 * bucket name is visible rather than silently reading as an empty bucket.
 *
 * Bucket names come from the tally provider, so they are only usable here if they are themselves
 * placeholder-safe: a bucket name containing `%` or a space cannot be addressed. A plugin whose
 * buckets are not placeholder-shaped should register its own placeholders against
 * [PlayerbaseTally.getCount] instead.
 *
 * Registration is idempotent per plugin **only** if the caller calls
 * [Leaderboards.unregisterAll] and re-registers in its reload handler: this helper never
 * unregisters anything itself.
 *
 * @param plugin    The plugin that owns the placeholders.
 * @param prefix    The placeholder prefix, usually the tally's unqualified ID.
 * @param emptyText The text to use for a bucket the last refresh did not produce.
 */
fun PlayerbaseTally.registerStandardPlaceholders(
    plugin: EcoPlugin,
    prefix: String,
    emptyText: String
) {
    val tally = this
    val pattern = Pattern.compile("${Pattern.quote(prefix)}_(.+)_count")

    DynamicPlaceholder(plugin, pattern) { args ->
        val matcher = pattern.matcher(args)

        if (!matcher.matches()) {
            return@DynamicPlaceholder emptyText
        }

        val bucket = matcher.group(1)
        val counts = tally.counts

        if (!counts.buckets.containsKey(bucket)) {
            return@DynamicPlaceholder emptyText
        }

        counts.get(bucket).toString()
    }.register()
}
