package com.willfp.eco.internal.spigot.proxy

import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.command.CommandMap

interface BukkitCommandsProxy {
    fun getCommandMap(): CommandMap

    fun syncCommands()

    fun unregisterCommand(command: PluginCommand)
}
