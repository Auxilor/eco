package com.willfp.eco.internal.spigot.actionbar

import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey

/**
 * What a player was last sent, written on the player's thread and read from netty threads.
 */
class PersistentActionBarState {
    @Volatile
    var lastComponent: Component? = null

    @Volatile
    var lastBarKey: NamespacedKey? = null

    @Volatile
    var lastSentAt = 0L

    @Volatile
    var pausedUntil = 0L

    @Volatile
    var selfSendAt = 0L

    @Volatile
    var forceRefresh = false
}
