package com.willfp.eco.internal.spigot.proxy.v26_1_1.common.recipes

import com.google.common.base.Function
import com.google.common.collect.Maps
import net.minecraft.core.registries.Registries
import net.minecraft.server.MinecraftServer
import net.minecraft.world.item.crafting.CraftingRecipe
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.ShapedRecipe
import net.minecraft.world.item.crafting.ShapedRecipePattern
import net.minecraft.world.item.crafting.ShapelessRecipe
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.craftbukkit.inventory.CraftRecipe
import org.bukkit.craftbukkit.inventory.CraftShapedRecipe
import org.bukkit.craftbukkit.inventory.CraftShapelessRecipe
import org.bukkit.craftbukkit.util.CraftNamespacedKey
import org.bukkit.inventory.Recipe as BukkitRecipe
import org.bukkit.inventory.RecipeChoice as BukkitRecipeChoice
import org.bukkit.inventory.ShapedRecipe as BukkitShapedRecipe
import org.bukkit.inventory.ShapelessRecipe as BukkitShapelessRecipe
import java.util.*

object RecipeManager {

    private fun getMinecraftRecipeManager() = MinecraftServer.getServer().resources.managers().recipeManager

    fun removeRecipeNoResend(recipeKey: NamespacedKey): Boolean {
        val recipeManager = getMinecraftRecipeManager()
        val toRemove = Bukkit.getRecipe(recipeKey) ?: return false
        val recipe = toRemove.toNMSEquivalent
        recipeManager.recipes.removeRecipe(recipe.id)
        return true
    }

    fun addRecipeNoResend(recipe: BukkitRecipe): Boolean {
        val recipeManager = getMinecraftRecipeManager()
        val toAdd: RecipeHolder<*> = recipe.toNMSEquivalent
        recipeManager.recipes.addRecipe(toAdd)
        return true
    }

    fun reloadRecipes() {
        val recipeManager = getMinecraftRecipeManager()
        recipeManager.finalizeRecipeLoading()
    }

    private fun replaceUndefinedIngredientsWithEmpty(
        shape: Array<String>,
        ingredients: MutableMap<Char, BukkitRecipeChoice?>
    ): List<String> {
        for (i in shape.indices) {
            val row = shape[i]
            val filteredRow = StringBuilder(row.length)
            for (character in row.toCharArray()) {
                filteredRow.append(if (ingredients[character] == null) ' ' else character)
            }
            shape[i] = filteredRow.toString()
        }
        return shape.toList()
    }

    private val BukkitRecipe.toNMSEquivalent: RecipeHolder<*>
        get() {
            when (this) {
                is BukkitShapedRecipe -> {
                    val craftRecipe = CraftShapedRecipe.fromBukkitRecipe(this)
                    val ingredients: MutableMap<Char, BukkitRecipeChoice?> = craftRecipe.getChoiceMap().filterKeys { c: Char? -> c != null }.toMutableMap()
                    val shape: List<String> = replaceUndefinedIngredientsWithEmpty(craftRecipe.shape, ingredients)
                    ingredients.values.removeIf { obj: BukkitRecipeChoice? -> Objects.isNull(obj) }
                    val recipeConverter: Function<BukkitRecipeChoice?, Ingredient> = Function { bukkit: BukkitRecipeChoice? ->
                        CraftRecipe.toIngredient(bukkit, false)
                    }
                    val data: Map<Char, Ingredient> = Maps.transformValues(ingredients, recipeConverter)
                    val pattern = ShapedRecipePattern.of(data, shape)
                    val recipe = ShapedRecipe(
                        Recipe.CommonInfo(true),
                        CraftingRecipe.CraftingBookInfo(CraftRecipe.getCategory(this.category), this.group),
                        pattern,
                        CraftItemStack.asTemplate(this.result)
                    )
                    return RecipeHolder(
                        CraftNamespacedKey.toResourceKey(Registries.RECIPE, this.key),
                        recipe
                    )
                }
                is BukkitShapelessRecipe -> {
                    val craftRecipe = CraftShapelessRecipe.fromBukkitRecipe(this)
                    val ingred: MutableList<BukkitRecipeChoice> = craftRecipe.getChoiceList()
                    val data: MutableList<Ingredient> = ArrayList(ingred.size)
                    for (i in ingred) {
                        data.add(CraftRecipe.toIngredient(i, true))
                    }
                    val recipe = ShapelessRecipe(
                        Recipe.CommonInfo(true),
                        CraftingRecipe.CraftingBookInfo(CraftRecipe.getCategory(this.category), this.group),
                        CraftItemStack.asTemplate(this.result),
                        data
                    )
                    return RecipeHolder(
                        CraftNamespacedKey.toResourceKey(Registries.RECIPE, this.key),
                        recipe
                    )
                }
                else -> {
                    throw UnsupportedOperationException("Unsupported recipe type: " + this.javaClass.name)
                }
            }
        }
}