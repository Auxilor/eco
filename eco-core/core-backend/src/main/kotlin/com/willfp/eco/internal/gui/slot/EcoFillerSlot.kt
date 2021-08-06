package com.willfp.eco.internal.gui.slot

import org.bukkit.inventory.ItemStack
import java.util.function.Function

class EcoFillerSlot(itemStack: ItemStack) : EcoSlot(
    Function { itemStack },
    { _, _ -> },
    { _, _ -> },
    { _, _ -> },
    { _, _ -> },
    { _, _ -> }
)