@file:JvmName("ProfileExtensions")

package com.willfp.eco.core.data

import org.bukkit.OfflinePlayer
import org.bukkit.Server

/** @see PlayerProfile.load */
val OfflinePlayer.profile: PlayerProfile
    get() = PlayerProfile.load(this)

/** @see ServerProfile.load */
val Server.profile: ServerProfile
    get() = ServerProfile.load()
