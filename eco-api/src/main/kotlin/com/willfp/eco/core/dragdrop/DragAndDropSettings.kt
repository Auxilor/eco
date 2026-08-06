package com.willfp.eco.core.dragdrop

/**
 * Behavioral knobs for a [DragAndDropHandler], supplied at registration time.
 */
data class DragAndDropSettings(
    /**
     * By default, drops are only handled in the player's own inventory. If true, drops are
     * also handled in the non-result slots of an open crafting inventory.
     */
    val allowCraftingTableNonResultSlot: Boolean = false
)
