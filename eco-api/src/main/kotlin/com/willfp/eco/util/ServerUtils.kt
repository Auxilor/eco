@file:JvmName("ServerUtilsExtensions")

package com.willfp.eco.util

import org.bukkit.Server

/**
 * The current TPS (ticks per second) of the running server, clamped to a maximum of 20.
 *
 * The receiver is ignored; this always reports the TPS of the server this plugin is running on.
 *
 * @see ServerUtils.getTps
 */
val Server.tps: Double
    get() = ServerUtils.getTps()
