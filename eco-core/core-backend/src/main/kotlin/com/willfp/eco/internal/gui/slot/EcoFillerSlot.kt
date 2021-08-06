package com.willfp.eco.internal.gui.slot

import org.bukkit.inventory.ItemStack
import java.util.function.Function

class EcoFillerSlot(itemStack: ItemStack) :
    EcoSlot(Function { itemStack }, null, null, null, null, null)