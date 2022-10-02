package com.willfp.eco.internal.spigot.recipes

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.isEmpty
import com.willfp.eco.core.recipe.Recipes
import com.willfp.eco.core.recipe.parts.GroupedTestableItems
import com.willfp.eco.core.recipe.parts.TestableStack
import com.willfp.eco.core.recipe.recipes.CraftingRecipe
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.CraftingInventory
import org.bukkit.inventory.ItemStack
import kotlin.math.max
import kotlin.math.min

class StackedRecipeListener(
    private val plugin: EcoPlugin
) : Listener {
    /*
    If you think you can fix this code, you're wrong.
    Or, pray to whatever god you have that you can figure it out.
    Best of luck, you're going to need it.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handleStacks(event: InventoryClickEvent) {
        val inventory = event.clickedInventory as? CraftingInventory ?: return
        if (event.slot != 0) {
            return
        }

        // Just in case
        if (inventory.getItem(event.slot).isEmpty) {
            return
        }

        val matrix = inventory.matrix

        val recipe = Recipes.getMatch(matrix) ?: return

        // Get the handler for the type of recipe
        @Suppress("UNCHECKED_CAST")
        val handler = handlers.firstOrNull { recipe::class.java.isAssignableFrom(it.recipeType) } ?: return

        var isStackedRecipe = false
        var maxCraftable = Int.MAX_VALUE

        // Start by calculating the maximum number of items to craft
        val maxToCraftData = handler.makeData(recipe)
        for (i in 0..8) {
            val item = inventory.matrix.getOrNull(i) ?: continue
            val part = handler.getPart(recipe, i, item, maxToCraftData).let {
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

        val deductionData = handler.makeData(recipe)
        for (i in 0..8) {
            val item = inventory.matrix.getOrNull(i) ?: continue
            val part = handler.getPart(recipe, i, item, deductionData).let {
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

            /*
            Everything below this point is unreadable garbage
            If you want to modify the behaviour of stacked recipes, then
            change the code above. The code after this just sets the items
            in the inventory, despite spigot trying to stop me.
             */

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

        // Modify the item and then set it
        if (event.isShiftClick) {
            existingResult.amount *= maxCraftable
        }
        inventory.result = existingResult
    }

    private fun runTwice(block: () -> Unit) {
        block()
        plugin.scheduler.run(block)
    }

    companion object {
        private val handlers = mutableListOf<StackedRecipeHandler>()

        fun registerHandler(handler: StackedRecipeHandler) {
            handlers.add(handler)
        }
    }
}

interface StackedRecipeHandler {
    fun makeData(recipe: CraftingRecipe): Any?
    fun getPart(recipe: CraftingRecipe, position: Int, item: ItemStack, data: Any?): TestableItem?
    val recipeType: Class<out CraftingRecipe>
}
