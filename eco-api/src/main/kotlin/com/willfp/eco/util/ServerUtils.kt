@file:JvmName("ServerUtilsExtensions")

package com.willfp.eco.util

import org.bukkit.Server

/** @see ServerUtils.getTps */
val Server.tps: Double
    get() = ServerUtils.getTps()
