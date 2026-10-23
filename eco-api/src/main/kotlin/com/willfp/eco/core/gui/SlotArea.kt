package com.willfp.eco.core.gui

import com.willfp.eco.core.config.interfaces.Config

/**
 * A rectangle of slots in a menu.
 *
 * @property row The first row.
 * @property column The first column.
 * @property rows The height in rows.
 * @property columns The width in columns.
 */
data class SlotArea(
    val row: Int,
    val column: Int,
    val rows: Int,
    val columns: Int
) {
    /** The number of slots in the area. */
    val size: Int
        get() = rows * columns
}

/**
 * Get the area a config section covers.
 *
 * Reads `path.top-left` and `path.bottom-right`, each with a `row` and `column`,
 * when `path.top-left` exists. Otherwise reads `path.row`, `path.column`,
 * `path.height` and `path.width`. Rows and columns are at least 0.
 *
 * @param path The path of the section, or empty for this config itself.
 * @return The slot area.
 */
fun Config.getSlotArea(path: String = ""): SlotArea {
    val prefix = if (path.isEmpty()) "" else "$path."

    if (has("${prefix}top-left")) {
        val topRow = getInt("${prefix}top-left.row")
        val leftColumn = getInt("${prefix}top-left.column")

        return SlotArea(
            row = topRow.coerceAtLeast(0),
            column = leftColumn.coerceAtLeast(0),
            rows = (getInt("${prefix}bottom-right.row") - topRow + 1).coerceAtLeast(0),
            columns = (getInt("${prefix}bottom-right.column") - leftColumn + 1).coerceAtLeast(0)
        )
    }

    return SlotArea(
        row = getInt("${prefix}row").coerceAtLeast(0),
        column = getInt("${prefix}column").coerceAtLeast(0),
        rows = getInt("${prefix}height").coerceAtLeast(0),
        columns = getInt("${prefix}width").coerceAtLeast(0)
    )
}
