package com.willfp.eco.core.gui.component

import com.willfp.eco.core.gui.SlotLocation
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.onLeftClick
import com.willfp.eco.core.gui.setUpdater
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.map.nestedMap
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil
import kotlin.properties.Delegates

private const val PROGRESSION_ORDER = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

/** The state of each level. */
enum class LevelState(
    val key: String
) {
    /** Level is unlocked / complete. */
    UNLOCKED("unlocked"),

    /** Level is currently being worked on. */
    IN_PROGRESS("in-progress"),

    /** Level has yet to be worked on. */
    LOCKED("locked");

    companion object {
        /** Get the state of [level] for a player at [currentLevel]. */
        @Suppress("FunctionNameMinLength")
        fun of(level: Int, currentLevel: Int): LevelState = when {
            level <= currentLevel -> UNLOCKED
            level == currentLevel + 1 -> IN_PROGRESS
            else -> LOCKED
        }
    }
}

/**
 * Read a progression [pattern] into the level offset of each slot.
 *
 * Characters follow `123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ`;
 * `0` and any other character are skipped. Rows and columns are 1-based.
 */
fun parseProgressionPattern(pattern: List<String>): Map<SlotLocation, Int> {
    val offsets = mutableMapOf<SlotLocation, Int>()

    for ((rowIndex, row) in pattern.withIndex()) {
        for ((columnIndex, character) in row.withIndex()) {
            val index = PROGRESSION_ORDER.indexOf(character)

            if (index != -1) {
                offsets[SlotLocation(row = rowIndex + 1, column = columnIndex + 1)] = index + 1
            }
        }
    }

    return offsets
}

/** Component to display level progression, for Skills/Jobs/etc. */
@Suppress("MemberVisibilityCanBePrivate")
abstract class LevelComponent : AutofillComponent() {
    private val slots = nestedMap<SlotLocation, Int, Slot?>()

    private var progressionSlots = emptyMap<SlotLocation, Int>()

    private var isBuilt = false

    abstract val pattern: List<String>

    abstract val maxLevel: Int

    /** Whether level items are refreshed while the menu is open. */
    open val updatesLive: Boolean = false

    private var _levelsPerPage by Delegates.notNull<Int>()

    val levelsPerPage: Int
        get() {
            if (!isBuilt) {
                build()
            }

            return _levelsPerPage
        }

    private var _pages by Delegates.notNull<Int>()

    val pages: Int
        get() {
            if (!isBuilt) {
                build()
            }

            return _pages
        }

    private fun buildSlot(level: Int) = Slot.builder { player, menu ->
        getLevelItem(player, menu, level, getLevelState(player, level))
    }.onLeftClick { player, _, _, menu ->
        onLevelClick(player, menu, level, getLevelState(player, level))
    }.apply {
        if (updatesLive) {
            setUpdater { player, menu, _ ->
                getLevelItem(player, menu, level, getLevelState(player, level))
            }
        }
    }.build()

    override fun getSlotAt(row: Int, column: Int, player: Player, menu: Menu): Slot? {
        if (!isBuilt) {
            build()
        }

        val position = SlotLocation(row, column)

        return slots[position].getOrPut(menu.getPage(player)) {
            val offset = progressionSlots[position] ?: return null
            val level = offset + (menu.getPage(player) - 1) * levelsPerPage

            if (level > maxLevel) {
                null
            } else {
                buildSlot(level)
            }
        }
    }

    private fun build() {
        isBuilt = true

        progressionSlots = parseProgressionPattern(pattern)
        _levelsPerPage = progressionSlots.size
        _pages = if (_levelsPerPage == 0) 0 else ceil(maxLevel.toDouble() / _levelsPerPage).toInt()
    }

    /** Get the page that shows [level]. */
    fun getPageOf(level: Int): Int {
        if (levelsPerPage == 0) {
            return 0
        }

        return ceil(level.toDouble() / levelsPerPage).toInt()
    }

    /** Get the item to be shown given a specific [level] and [levelState]. */
    abstract fun getLevelItem(player: Player, menu: Menu, level: Int, levelState: LevelState): ItemStack

    /** Get the state given a [player]'s [level]. */
    abstract fun getLevelState(player: Player, level: Int): LevelState

    /** Called when a player left clicks the slot of [level]. */
    open fun onLevelClick(player: Player, menu: Menu, level: Int, levelState: LevelState) {}
}
