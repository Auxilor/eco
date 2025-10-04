package com.willfp.eco.internal.spigot.proxies

import com.willfp.eco.core.command.PluginCommandBase
import org.bukkit.command.CommandMap

interface BukkitCommandsProxy {
    fun getCommandMap(): CommandMap

    fun syncCommands()

    fun unregisterCommand(command: PluginCommandBase)
}
