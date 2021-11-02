package com.willfp.eco.internal.display

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.display.DisplayHandler
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.display.DisplayPriority
import com.willfp.eco.core.fast.FastItemStack
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class EcoDisplayHandler(plugin: EcoPlugin) : DisplayHandler {
    private val registeredModules = mutableMapOf<DisplayPriority, MutableList<DisplayModule>>()
    private val finalizeKey: NamespacedKey = plugin.namespacedKeyFactory.create("finalized")

    init {
        for (priority in DisplayPriority.values()) {
            registeredModules[priority] = mutableListOf()
        }
    }

    override fun registerDisplayModule(module: DisplayModule) {
        val modules = registeredModules[module.priority] ?: return
        modules.removeIf { module1: DisplayModule ->
            module1.pluginName.equals(module.pluginName, ignoreCase = true)
        }
        modules.add(module)
        registeredModules[module.priority] = modules
    }

    override fun display(itemStack: ItemStack, player: Player?): ItemStack {
        val pluginVarArgs = mutableMapOf<String, Array<Any>>()

        for (priority in DisplayPriority.values()) {
            val modules = registeredModules[priority] ?: continue
            for (module in modules) {
                pluginVarArgs[module.pluginName] = module.generateVarArgs(itemStack)
            }
        }

        Display.revert(itemStack)

        itemStack.itemMeta ?: return itemStack

        for (priority in DisplayPriority.values()) {
            val modules = registeredModules[priority] ?: continue
            for (module in modules) {
                val varargs = pluginVarArgs[module.pluginName] ?: continue
                Display.callDisplayModule(module, itemStack, player, *varargs)
            }
        }

        return itemStack
    }

    override fun revert(itemStack: ItemStack): ItemStack {
        if (Display.isFinalized(itemStack)) {
            Display.unfinalize(itemStack)
        }

        val fast = FastItemStack.wrap(itemStack)
        val lore = fast.lore

        if (lore.isNotEmpty() && lore.removeIf { line: String ->
                line.startsWith(
                    Display.PREFIX
                )
            }) { // Only modify lore if needed.
            fast.lore = lore
        }

        for (priority in DisplayPriority.values()) {
            val modules = registeredModules[priority] ?: continue
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

        val meta = itemStack.itemMeta ?: return itemStack

        val container = meta.persistentDataContainer
        container.set(finalizeKey, PersistentDataType.INTEGER, 1)
        itemStack.itemMeta = meta
        return itemStack
    }

    override fun unfinalize(itemStack: ItemStack): ItemStack {
        val meta = itemStack.itemMeta ?: return itemStack

        val container = meta.persistentDataContainer
        container.remove(finalizeKey)
        itemStack.itemMeta = meta
        return itemStack
    }

    override fun isFinalized(itemStack: ItemStack): Boolean {
        val meta = itemStack.itemMeta ?: return false

        val container = meta.persistentDataContainer
        return container.has(finalizeKey, PersistentDataType.INTEGER)
    }
}