package com.willfp.eco.core.dragdrop

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface DragAndDropHandler {
    val id: String

    fun matches(cursor: ItemStack, current: ItemStack): Boolean

    fun apply(player: Player, cursor: ItemStack, current: ItemStack): DragAndDropResult
}
