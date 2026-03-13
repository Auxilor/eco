package com.willfp.eco.internal.spigot.proxies

import com.willfp.eco.core.EcoPlugin
import org.bukkit.NamespacedKey
import org.bukkit.inventory.Recipe

interface CommonsInitializerProxy {
    fun init(plugin: EcoPlugin)

    fun addBukkitRecipeNoResend(recipe: Recipe)

    fun reloadBukkitRecipes()

    fun removeBukkitRecipeNoResend(key: NamespacedKey): Boolean
}
