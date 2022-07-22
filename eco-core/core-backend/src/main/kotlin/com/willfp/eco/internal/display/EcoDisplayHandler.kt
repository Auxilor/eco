package com.willfp.eco.internal.display

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.display.DisplayHandler
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.display.DisplayProperties
import com.willfp.eco.core.fast.fast
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class EcoDisplayHandler(plugin: EcoPlugin) : DisplayHandler {
    private val registeredModules = sortedMapOf<Int, MutableList<DisplayModule>>()
    private val finalizeKey: NamespacedKey = plugin.namespacedKeyFactory.create("finalized")

    override fun registerDisplayModule(module: DisplayModule) {
        val modules = registeredModules[module.weight] ?: mutableListOf()
        modules.removeIf {
            it.pluginName.equals(module.pluginName, ignoreCase = true)
        }
        modules.add(module)
        registeredModules[module.weight] = modules
    }

    override fun display(itemStack: ItemStack, player: Player?): ItemStack {
        val pluginVarArgs = mutableMapOf<String, Array<Any>>()

        for ((_, modules) in registeredModules) {
            for (module in modules) {
                pluginVarArgs[module.pluginName] = module.generateVarArgs(itemStack)
            }
        }

        Display.revert(itemStack)

        if (!itemStack.hasItemMeta()) {
            return itemStack
        }

        val original = itemStack.clone()
        val inventory = player?.openInventory?.topInventory
        val inInventory = inventory?.contains(original) ?: false

        val props = DisplayProperties(
            inInventory,
            inInventory && inventory?.holder == null,
            original
        )

        for ((_, modules) in registeredModules) {
            for (module in modules) {
                val varargs = pluginVarArgs[module.pluginName] ?: continue

                module.display(itemStack, *varargs)

                if (player != null) {
                    module.display(itemStack, player as Player?, *varargs)
                    module.display(itemStack, player as Player?, props, *varargs)
                }
            }
        }

        return itemStack
    }

    override fun revert(itemStack: ItemStack): ItemStack {
        if (Display.isFinalized(itemStack)) {
            Display.unfinalize(itemStack)
        }

        val fast = itemStack.fast()
        val lore = fast.lore

        if (lore.isNotEmpty() && lore.removeIf { line: String ->
                line.startsWith(
                    Display.PREFIX
                )
            }) { // Only modify lore if needed.
            fast.lore = lore
        }

        for ((_, modules) in registeredModules) {
            for (module in modules) {
                module.revert(itemStack)
            }
        }

        return itemStack
    }

    override fun finalize(itemStack: ItemStack): ItemStack {
        if (itemStack.type.maxStackSize > 1) {
            return itemStack
        }

        itemStack.fast().persistentDataContainer.set(finalizeKey, PersistentDataType.INTEGER, 1)

        return itemStack
    }

    override fun unfinalize(itemStack: ItemStack): ItemStack {
        itemStack.fast().persistentDataContainer.remove(finalizeKey)

        return itemStack
    }

    override fun isFinalized(itemStack: ItemStack): Boolean {
        return itemStack.fast().persistentDataContainer.has(finalizeKey, PersistentDataType.INTEGER)
    }
}
