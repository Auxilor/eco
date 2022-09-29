package com.willfp.eco.internal.gui.slot

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

open class EcoFillerSlot(itemStack: ItemStack) : EcoSlot(
    { _, _ -> itemStack },
    { _, _, _ -> },
    { _, _, _ -> },
    { _, _, _ -> },
    { _, _, _ -> },
    { _, _, _ -> },
    { _, _, prev -> prev }
)

object EmptyFillerSlot : EcoFillerSlot(ItemStack(Material.AIR))
