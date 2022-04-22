package com.willfp.eco.internal.spigot

import com.willfp.eco.core.Eco
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.items.Items
import com.willfp.eco.util.toLegacy
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object Debug : Listener {
    @EventHandler
    fun a(event: AsyncChatEvent) {
        val player = event.player
        val text = event.message().toLegacy()

        val words = text.split(" ")

        if (words[0] != "debug") {
            return
        }

        Eco.getHandler().ecoPlugin.scheduler.run {
            val speed = words[1].toDouble()

            val item = Items.lookup(words.subList(2, words.size - 1).joinToString(" ")).item
            Items.setDestroySpeedMultiplier(item, speed)
            DropQueue(player)
                .addItem(item)
                .forceTelekinesis()
                .push()
        }

    }
}