package com.willfp.eco.core.dragdrop

/**
 * The outcome of a [DragAndDropHandler.apply] call.
 */
enum class DragAndDropResult {
    /** The drop was applied, so the shell consumes one item from the cursor. */
    APPLIED,

    /** The drop was refused, so the cursor is left untouched. The click is still cancelled. */
    DENIED
}
