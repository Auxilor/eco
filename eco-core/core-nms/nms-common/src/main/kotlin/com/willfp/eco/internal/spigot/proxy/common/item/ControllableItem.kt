package com.willfp.eco.internal.spigot.proxy.common.item

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState

class ControllableItem(
    val handle: Item
) : Item(
    Properties()
        .apply {
            handle.itemCategory?.let { tab(it) }
            durability(handle.maxDamage)
            rarity(handle.rarity)
            stacksTo(handle.maxStackSize)
            handle.craftingRemainingItem?.let { craftRemainder(it) }
            if (handle.isFireResistant) fireResistant()
            handle.foodProperties?.let { food(it) }
        }
) {
    var destroySpeedMultiplier: Double = 1.0

    override fun getDestroySpeed(stack: ItemStack, state: BlockState): Float {
        return handle.getDestroySpeed(stack, state) * destroySpeedMultiplier.toFloat()
    }
}
