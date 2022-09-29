package com.willfp.eco.internal.gui.slot

import com.willfp.eco.core.gui.slot.functional.SlotHandler
import org.bukkit.inventory.ItemStack

private val noop = SlotHandler { _, _, _ -> }

class EcoFillerSlot(itemStack: ItemStack) : EcoSlot(
    { _, _ -> itemStack },
    noop,
    noop,
    noop,
    noop,
    noop,
    { _, _, prev -> prev }
)
