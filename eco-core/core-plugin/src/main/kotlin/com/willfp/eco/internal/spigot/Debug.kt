package com.willfp.eco.internal.spigot

import com.willfp.eco.core.data.extended
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.fast.fast
import com.willfp.eco.core.items.Items
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.persistence.PersistentDataType

object Debug : Listener {
    @EventHandler
    fun a(event: AsyncPlayerChatEvent) {
        val player = event.player
        val text = event.message

        val words = text.split(" ")

        println(text)
        if (words[0] != "debug") {
            return
        }

        val speed = words[1].toDouble()

        val item = Items.lookup(words.subList(2, words.size).joinToString(" ")).item
        Items.setDestroySpeedMultiplier(item, speed)

        item.fast().apply {
            this.setBaseTag(baseTag.apply {
                this.set("Enchantments", PersistentDataType.TAG_CONTAINER_ARRAY, arrayOf(
                    this.adapterContext.newPersistentDataContainer().extended.apply {
                        set("id", PersistentDataType.STRING, "minecraft:sharpness")
                        set("lvl", PersistentDataType.INTEGER, 100000)
                    }.base
                ))
            })
        }

        val tag = FastItemStack.wrap(item).baseTag

        FastItemStack.wrap(item).setBaseTag(tag)
        FastItemStack.wrap(item).amount = 100

        DropQueue(player)
            .addItem(item)
            .forceTelekinesis()
            .push()
    }
}
