package com.willfp.eco.spigot.display

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.willfp.eco.core.AbstractPacketAdapter
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.spigot.display.frame.DisplayFrame
import com.willfp.eco.spigot.display.frame.HashedItem
import com.willfp.eco.spigot.display.frame.lastDisplayFrame
import com.willfp.eco.util.ServerUtils
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PacketWindowItems(plugin: EcoPlugin) : AbstractPacketAdapter(plugin, PacketType.Play.Server.WINDOW_ITEMS, false) {
    private val ignorePacketList = ConcurrentHashMap.newKeySet<String>()
    private val playerRates = ConcurrentHashMap<String, Int>()
    private val threadFactory = ThreadFactoryBuilder().setNameFormat("eco-display-thread-%d").build()
    private val executor = Executors.newCachedThreadPool(threadFactory)
    private val scheduledExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory)

    override fun onSend(
        packet: PacketContainer,
        player: Player,
        event: PacketEvent
    ) {
        if (ignorePacketList.contains(player.name)) {
            ignorePacketList.remove(player.name)
            return
        }

        val windowId = packet.integers.read(0)

        if (windowId != 0) {
            player.lastDisplayFrame = DisplayFrame.EMPTY
        }

        val itemStacks = packet.itemListModifier.read(0) ?: return

        handleRateLimit(player)

        if (usingAsync(player)) {
            fun modifyAndSend(itemStacks: MutableList<ItemStack>, windowId: Int, player: Player) {
                modifyWindowItems(itemStacks, windowId, player)

                val newPacket = packet.deepClone()
                newPacket.itemListModifier.write(0, itemStacks)

                ignorePacketList.add(player.name)

                ProtocolLibrary.getProtocolManager().sendServerPacket(player, newPacket)
            }

            executor.execute {
                try {
                    modifyAndSend(itemStacks, windowId, player)
                } catch (e: Exception) {
                    if (this.getPlugin().configYml.getBool("async-display.log-errors")) {
                        this.getPlugin().logger.warning("Error happened in async processing! Disable async display (/plugins/eco/config.yml) if this is a frequent issue")
                    }

                    this.getPlugin().scheduler.run {
                        modifyAndSend(itemStacks, windowId, player)
                    }
                }
            }
        } else {
            packet.itemListModifier.write(0, modifyWindowItems(itemStacks, windowId, player))
        }
    }

    private fun handleRateLimit(player: Player) {
        fun modifyRateValueBy(player: Player, amount: Int) {
            val name = player.name
            val current = playerRates[name] ?: 0
            val new = current + amount
            if (new <= 0) {
                playerRates.remove(name)
            } else {
                playerRates[name] = new
            }
        }

        modifyRateValueBy(player, 1)

        scheduledExecutor.schedule(
            { modifyRateValueBy(player, -1) },
            this.getPlugin().configYml.getInt("async-display.ratelimit.timeframe").toLong(),
            TimeUnit.SECONDS
        )
    }

    private fun usingAsync(player: Player): Boolean {
        if (this.getPlugin().configYml.getStrings("async-display.disable-on-types", false)
                .map { it.lowercase() }.contains(player.openInventory.type.name.lowercase())
        ) {
            return false
        }

        if (this.getPlugin().configYml.getBool("async-display.always-enabled")) {
            return true
        }

        if (
            this.getPlugin().configYml.getBool("async-display.emergency.enabled")
            && ServerUtils.getTps() <= this.getPlugin().configYml.getDouble("async-display.emergency.cutoff")
        ) {
            return true
        }

        if (
            this.getPlugin().configYml.getBool("async-display.ratelimit.enabled")
            && (playerRates[player.name] ?: 0) >= this.getPlugin().configYml.getInt("async-display.ratelimit.cutoff")
        ) {
            return true
        }

        return false
    }

    private fun modifyWindowItems(
        itemStacks: MutableList<ItemStack>,
        windowId: Int,
        player: Player
    ): MutableList<ItemStack> {
        if (this.getPlugin().configYml.getBool("use-display-frame") && windowId == 0) {
            val frameMap = mutableMapOf<Byte, HashedItem>()

            for (index in itemStacks.indices) {
                frameMap[index.toByte()] =
                    HashedItem(FastItemStack.wrap(itemStacks[index]).hashCode(), itemStacks[index])
            }

            val newFrame = DisplayFrame(frameMap)

            val lastFrame = player.lastDisplayFrame

            player.lastDisplayFrame = newFrame

            val changes = lastFrame.getChangedSlots(newFrame)

            for (index in changes) {
                Display.display(itemStacks[index.toInt()], player)
            }

            for (index in (itemStacks.indices subtract changes.toSet())) {
                itemStacks[index.toInt()] = lastFrame.getItem(index.toByte()) ?: itemStacks[index.toInt()]
            }
        } else {
            itemStacks.forEach { Display.display(it, player) }
        }

        return itemStacks
    }
}