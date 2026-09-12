package com.willfp.eco.util

import org.bukkit.entity.Player

/**
 * Resolving a number carried in a permission node, e.g. `myplugin.xpmultiplier.150`.
 *
 * Four plugins each grew their own copy of this loop, and they did not agree. Three shared a
 * defect that silently ignored negative values, and the fourth returned whichever match it
 * happened to iterate first - which, since permissions are unordered, meant a player holding
 * two of them could get a different answer across a relog with no config change.
 *
 * The resolution itself is [highest], which takes plain strings so it can be tested without a
 * server; [Player.getNumericalPermission] is the thin Bukkit wrapper over it.
 *
 * ## Only the logic is shared, never the permission nodes
 *
 * eco declares no permission of its own here, and there is deliberately no cross-plugin node.
 * Every caller passes its own prefix - `ecoskills.xpmultiplier`, `ecojobs.xpmultiplier`, and so
 * on - and declares those nodes in its own `plugin.yml`, where they stay `default: false`. A
 * shared node would mean granting one plugin's multiplier silently granted every other
 * plugin's too, which is precisely what a server owner would not expect.
 */
object NumericalPermissions {
    /**
     * The highest number carried by any permission in [permissions] starting with [prefix].
     *
     * ## Highest, not first
     *
     * Permissions are unordered, so "first match" is not a rule, it is a coin flip: a player
     * granted both `prefix.150` and `prefix.300` through different groups would get either.
     * Taking the maximum makes the result depend only on what the player holds.
     *
     * Negative values count, and can therefore lower the result below zero - a `prefix.-50` is
     * read as -50, not ignored. The copies this replaces seeded their running maximum with
     * `Double.MIN_VALUE`, which is the smallest *positive* double rather than the most
     * negative one, so any negative grant lost to the seed and silently did nothing.
     *
     * A suffix that is not a number is skipped rather than counted as a default, so a typo
     * such as `prefix.abc` contributes nothing instead of quietly resolving to some value.
     *
     * @param permissions The permission nodes the holder has, already filtered to those that
     *                    are actually granted - a node set to false must not be passed in.
     * @param prefix      The node prefix to match, without a trailing dot.
     * @param default     Returned when nothing matches.
     * @return The highest matching value, or [default].
     */
    @JvmStatic
    fun highest(permissions: Iterable<String>, prefix: String, default: Double): Double {
        var highest: Double? = null

        for (permission in permissions) {
            if (!permission.startsWith(prefix)) {
                continue
            }

            val value = permission.substringAfterLast(".").toDoubleOrNull() ?: continue

            highest = if (highest == null) value else maxOf(highest, value)
        }

        return highest ?: default
    }
}

/**
 * The highest number carried by any permission this player holds starting with [prefix].
 *
 * Only permissions that are actually granted are considered; one explicitly set to false is
 * ignored, which the hand-rolled copies of this loop did not do - a negated node still counted
 * toward the result there.
 *
 * @param prefix  The node prefix to match, without a trailing dot.
 * @param default Returned when the player holds no matching permission.
 * @return The highest matching value, or [default].
 * @see NumericalPermissions.highest
 */
fun Player.getNumericalPermission(prefix: String, default: Double): Double =
    NumericalPermissions.highest(
        this.effectivePermissions.filter { it.value }.map { it.permission },
        prefix,
        default
    )
