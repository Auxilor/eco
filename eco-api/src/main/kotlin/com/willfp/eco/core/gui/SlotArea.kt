package com.willfp.eco.core.gui

import com.willfp.eco.core.config.interfaces.Config

/**
 * A rectangle of slots in a menu.
 *
 * @param row The first row.
 * @param column The first column.
 * @param rows The height in rows.
 * @param columns The width in columns.
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
            topRow.coerceAtLeast(0),
            leftColumn.coerceAtLeast(0),
            (getInt("${prefix}bottom-right.row") - topRow + 1).coerceAtLeast(0),
            (getInt("${prefix}bottom-right.column") - leftColumn + 1).coerceAtLeast(0)
        )
    }

    return SlotArea(
        getInt("${prefix}row").coerceAtLeast(0),
        getInt("${prefix}column").coerceAtLeast(0),
        getInt("${prefix}height").coerceAtLeast(0),
        getInt("${prefix}width").coerceAtLeast(0)
    )
}
