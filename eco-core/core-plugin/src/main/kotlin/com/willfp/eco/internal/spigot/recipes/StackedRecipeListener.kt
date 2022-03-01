package com.willfp.eco.internal.spigot.recipes

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.recipe.Recipes
import com.willfp.eco.core.recipe.parts.GroupedTestableItems
import com.willfp.eco.core.recipe.parts.TestableStack
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.CraftingInventory
import kotlin.math.max
import kotlin.math.min

class StackedRecipeListener(
    private val plugin: EcoPlugin
) : Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handleStacks(event: InventoryClickEvent) {
        val inventory = event.clickedInventory as? CraftingInventory ?: return
        if (event.slot != 0) {
            return
        }

        val matrix = inventory.matrix

        val recipe = Recipes.getMatch(matrix) ?: return

        var isStackedRecipe = false
        var maxCraftable = Int.MAX_VALUE

        // Start by calculating the maximum number of items to craft
        for (i in 0..8) {
            val item = inventory.matrix.getOrNull(i) ?: continue
            val part = recipe.parts[i].let {
                if (it is GroupedTestableItems) {
                    it.getMatchingChild(item)
                } else it
            } ?: continue

            if (part is TestableStack) {
                isStackedRecipe = true
            }

            maxCraftable = min(maxCraftable, Math.floorDiv(item.amount, part.item.amount))
        }

        if (!isStackedRecipe) {
            return
        }

        // Don't allow crafting above the max stack size of the output
        maxCraftable = min(maxCraftable, Math.floorDiv(recipe.output.maxStackSize, recipe.output.amount))

        // Run this first before the deduction or shift-clicking breaks
        val existingResult = inventory.result

        // Deduct the correct number of items from the inventory
        for (i in 0..8) {
            val item = inventory.matrix.getOrNull(i) ?: continue
            val part = recipe.parts[i].let {
                if (it is GroupedTestableItems) {
                    it.getMatchingChild(item)
                } else it
            } ?: continue

            val amount = max(
                if (event.isShiftClick) {
                    item.amount - (part.item.amount * maxCraftable)
                } else {
                    item.amount - part.item.amount
                }, 0
            )

            // Anti-Underflow
            if (amount == 0) {
                item.type = Material.AIR
            }
            item.amount = amount

            // Do it twice because spigot hates me
            // Everything has to be cloned because the inventory changes the item
            inventory.matrix[i] = item.clone() // Use un-cloned version first
            // This isn't even funny anymore
            runTwice {
                val newItem = item.clone()
                // Just use every method possible to set the item
                inventory.matrix[i] = newItem
                inventory.setItem(i + 1, newItem)
                // Just to be safe, modify the instance (safe check) Using ?. causes a warning.
                @Suppress("SENSELESS_COMPARISON") // I hate compiler warnings
                if (inventory.matrix[i] != null) {
                    inventory.matrix[i].amount = amount
                }
            }
        }

        // Multiply the result by the amount to craft if shift-clicking
        existingResult ?: return

        // Run later as will be overridden by above check
        plugin.scheduler.run {
            if (event.isShiftClick) {
                existingResult.amount *= maxCraftable
            }
            inventory.result = existingResult
        }
    }

    private fun runTwice(block: () -> Unit) {
        block()
        plugin.scheduler.run(block)
    }
}
