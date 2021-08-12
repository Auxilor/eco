package com.willfp.eco.internal.gui.slot

import org.bukkit.inventory.ItemStack

class EcoFillerSlot(itemStack: ItemStack) : EcoSlot(
    { _, _ -> itemStack },
    { _, _, _ -> },
    { _, _, _ -> },
    { _, _, _ -> },
    { _, _, _ -> },
    { _, _, _ -> },
    { _, _, _ -> }
)