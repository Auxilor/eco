package com.willfp.eco.internal.spigot.proxy.v1_21_7

import com.willfp.eco.core.command.PluginCommandBase
import com.willfp.eco.internal.spigot.proxy.BukkitCommandsProxy
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.SimpleCommandMap
import org.bukkit.craftbukkit.CraftServer
import java.lang.reflect.Field

class BukkitCommands : BukkitCommandsProxy {
    private val knownCommandsField: Field by lazy {
        SimpleCommandMap::class.java.getDeclaredField("knownCommands")
            .apply {
                isAccessible = true
            }
    }

    @Suppress("UNCHECKED_CAST")
    private val knownCommands: MutableMap<String, Command>
        get() = knownCommandsField.get(getCommandMap()) as MutableMap<String, Command>

    override fun getCommandMap(): SimpleCommandMap {
        return (Bukkit.getServer() as CraftServer).commandMap
    }

    override fun syncCommands() {
        (Bukkit.getServer() as CraftServer).syncCommands()
    }

    override fun unregisterCommand(command: PluginCommandBase) {
        knownCommands.remove(command.name)
        knownCommands.remove("${command.plugin.name.lowercase()}:${command.name}")
    }
}
