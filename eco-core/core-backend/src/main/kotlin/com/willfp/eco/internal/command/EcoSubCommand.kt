package com.willfp.eco.internal.command

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.CommandBase

/**
 * EcoSubCommand is just an instantiated handled command.<br>
 * The main difference from EcoPluginCommand is that there is no registration
 * of subcommands because they are called and added from their parent.
 */
class EcoSubCommand(
    parentDelegate: CommandBase,
    plugin: EcoPlugin,
    name: String,
    permission: String,
    playersOnly: Boolean
) : EcoHandledCommand(parentDelegate, plugin, name, permission, playersOnly) {

}