package com.willfp.eco.internal.command

import com.willfp.eco.core.EcoPlugin

/**
 * EcoSubCommand is just an instantiated handled command.<br>
 * The main difference from EcoPluginCommand is that there is no registration
 * of subcommands because they are called and added from their parent.
 */
class EcoSubCommand(
    plugin: EcoPlugin, name: String,
    permission: String,
    playersOnly: Boolean
) : EcoHandledCommand(plugin, name, permission, playersOnly) {

}