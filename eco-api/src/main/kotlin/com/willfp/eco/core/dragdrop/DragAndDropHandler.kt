package com.willfp.eco.core.dragdrop

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Plugin-supplied rules for applying the item on the cursor to the item that was clicked,
 * i.e. dropping one item onto another in an inventory.
 *
 * Register with [DragAndDropHandlers]; the eco shell owns the event handling, the slot and
 * inventory checks, and consuming one item from the cursor when the drop is applied.
 */
interface DragAndDropHandler {
    /**
     * The ID of the handler.
     *
     * Registering a handler replaces any handler already registered under the same ID.
     * Prefixing the ID with `pluginname:` allows [DragAndDropHandlers.unregisterAll] to
     * remove every handler belonging to a plugin at once.
     */
    val id: String

    /**
     * Check whether this handler applies to the given item pair.
     *
     * Called before [apply]; the shell only calls [apply] if this returns true.
     *
     * @param cursor  The item on the cursor, never empty.
     * @param current The item that was clicked, never empty.
     * @return If this handler handles the drop.
     */
    fun matches(cursor: ItemStack, current: ItemStack): Boolean

    /**
     * Apply the drop.
     *
     * The clicked item may be mutated in place. Returning [DragAndDropResult.APPLIED] makes
     * the shell consume one item from the cursor; the click is cancelled either way.
     *
     * @param player  The player.
     * @param cursor  The item on the cursor.
     * @param current The item that was clicked.
     * @return The result.
     */
    fun apply(player: Player, cursor: ItemStack, current: ItemStack): DragAndDropResult
}
