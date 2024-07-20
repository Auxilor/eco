package com.willfp.eco.internal.spigot.recipes

import com.willfp.eco.core.recipe.recipes.CraftingRecipe
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.event.Event
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.CraftingInventory
import org.bukkit.inventory.ItemStack

interface RecipeValidator {
    fun validate(event: GenericCraftEvent): Boolean
}

interface RecipeListener {
    fun handle(event: GenericCraftEvent)
}

interface GenericCraftEvent {
    val inventory: CraftingInventory
    val recipe: Keyed

    fun allow(recipe: CraftingRecipe)
    fun deny()
}

class WrappedPrepareItemCraftEvent(
    private val event: PrepareItemCraftEvent,
    override val recipe: Keyed
) : GenericCraftEvent {
    override val inventory: CraftingInventory
        get() = event.inventory

    override fun allow(recipe: CraftingRecipe) {
        this.inventory.result = recipe.output
    }

    override fun deny() {
        this.inventory.result = ItemStack(Material.AIR)
    }
}

class WrappedCraftItemEvent(
    private val event: CraftItemEvent
) : GenericCraftEvent {
    override val inventory: CraftingInventory
        get() = event.inventory

    override val recipe: Keyed
        get() = event.recipe as Keyed

    override fun allow(recipe: CraftingRecipe) {
        this.inventory.result = recipe.output
    }

    override fun deny() {
        this.inventory.result = ItemStack(Material.AIR)
        event.result = Event.Result.DENY
        event.isCancelled = true
    }
}
