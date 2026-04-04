package com.willfp.eco.internal.spigot.proxy.common.recipes

import com.google.common.base.Function
import com.google.common.collect.Maps
import net.minecraft.server.MinecraftServer
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.ShapedRecipePattern
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.craftbukkit.inventory.CraftRecipe
import org.bukkit.craftbukkit.inventory.CraftShapedRecipe
import org.bukkit.craftbukkit.inventory.CraftShapelessRecipe
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import java.util.*

object RecipeManager {
    fun removeRecipeNoResend(recipeKey: NamespacedKey): Boolean {
        val recipeManager = MinecraftServer.getServer().resources.managers().recipeManager

        val toRemove = Bukkit.getRecipe(recipeKey) ?: return false

        val recipe = toRemove.toNMSEquivalent

        @Suppress("UNCHECKED_CAST")
        val recipeKey = recipe.id as net.minecraft.resources
            .ResourceKey<net.minecraft.world.item.crafting.Recipe<net.minecraft.world.item.crafting.RecipeInput>>

        recipeManager.recipes.removeRecipe(recipeKey)

        return true
    }

    fun addRecipeNoResend(recipe: Recipe): Boolean {
        val recipeManager = MinecraftServer.getServer().resources.managers().recipeManager

        val toAdd: RecipeHolder<*> = recipe.toNMSEquivalent

        recipeManager.recipes.addRecipe(toAdd)

        return true
    }

    fun reloadRecipes() {
        val recipeManager = MinecraftServer.getServer().resources.managers().recipeManager

        recipeManager.finalizeRecipeLoading()
    }

    private fun replaceUndefinedIngredientsWithEmpty(
        shape: Array<String>,
        ingredients: MutableMap<Char?, RecipeChoice?>
    ): Array<String> {
        for (i in shape.indices) {
            val row = shape[i]
            val filteredRow = StringBuilder(row.length)

            for (character in row.toCharArray()) {
                filteredRow.append(if (ingredients.get(character) == null) ' ' else character)
            }

            shape[i] = filteredRow.toString()
        }

        return shape
    }

    private val Recipe.toNMSEquivalent: RecipeHolder<*>
        get() {
            if (this is ShapedRecipe) {
                val craftRecipe = CraftShapedRecipe.fromBukkitRecipe(this)
                val ingred: MutableMap<Char?, RecipeChoice?> = craftRecipe.getChoiceMap()
                val shape = replaceUndefinedIngredientsWithEmpty(craftRecipe.getShape(), ingred)
                ingred.values.removeIf { obj: RecipeChoice? -> Objects.isNull(obj) }
                val data = Maps.transformValues<Char?, RecipeChoice?, Ingredient?>(
                    ingred,
                    Function { bukkit: RecipeChoice? -> craftRecipe.toNMS(bukkit, false) })
                val pattern = ShapedRecipePattern.of(data, *shape)
                return RecipeHolder(
                    CraftRecipe.toMinecraft(craftRecipe.getKey()),
                    net.minecraft.world.item.crafting.ShapedRecipe(
                        craftRecipe.getGroup(),
                        CraftRecipe.getCategory(craftRecipe.getCategory()),
                        pattern,
                        CraftItemStack.asNMSCopy(craftRecipe.getResult())
                    )
                )
            } else if (this is ShapelessRecipe) {
                val craftRecipe = CraftShapelessRecipe.fromBukkitRecipe(this)
                val ingred: MutableList<RecipeChoice> = craftRecipe.getChoiceList()
                val data: MutableList<Ingredient?> = ArrayList(ingred.size)

                for (i in ingred) {
                    data.add(craftRecipe.toNMS(i, true))
                }

                return RecipeHolder(
                    CraftRecipe.toMinecraft(craftRecipe.getKey()),
                    net.minecraft.world.item.crafting.ShapelessRecipe(
                        craftRecipe.getGroup(),
                        CraftRecipe.getCategory(craftRecipe.getCategory()),
                        CraftItemStack.asNMSCopy(craftRecipe.getResult()),
                        data
                    )
                )
            } else {
                throw UnsupportedOperationException("Unsupported recipe type: " + this.javaClass.name)
            }
        }
}