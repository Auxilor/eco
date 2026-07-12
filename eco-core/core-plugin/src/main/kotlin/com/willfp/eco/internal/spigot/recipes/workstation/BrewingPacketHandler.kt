package com.willfp.eco.internal.spigot.recipes.workstation

import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.core.recipe.workstation.BrewingRecipe
import com.willfp.eco.core.recipe.workstation.WorkstationRecipes
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask

class BrewingPacketHandler(private val plugin: Plugin) : PacketListener, Listener {

    private val pendingBrews = mutableMapOf<Location, BukkitTask>()
    private val progressTasks = mutableMapOf<Location, BukkitTask>()

    init {
        WorkstationRecipes.registerBrewCancelHook { cancelBrew(it) }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onShiftClickIngredient(event: InventoryClickEvent) {
        if (event.inventory.type != InventoryType.BREWING) return
        if (!event.isShiftClick) return
        val player = event.whoClicked as? Player ?: return
        val location = event.inventory.location ?: return
        if (location in pendingBrews) return
        Bukkit.getScheduler().runTask(plugin, Runnable {
            val brewer = (location.block.state as? org.bukkit.block.BrewingStand)?.inventory ?: return@Runnable
            val ingredient = brewer.ingredient ?: return@Runnable
            val recipe = WorkstationRecipes.getAll(BrewingRecipe::class.java)
                .firstOrNull {
                    it.ingredient.matches(ingredient) &&
                    (0..2).any { slot -> it.base.matches(brewer.getItem(slot)) }
                } ?: return@Runnable
            scheduleBrew(location, recipe, player)
        })
    }

    override fun onReceive(event: PacketEvent) {
        val packet = event.packet.handle
        if (!packet.javaClass.name.endsWith("ServerboundContainerClickPacket")) return
        val slotNum = runCatching {
            packet.javaClass.getDeclaredField("slotNum").apply { isAccessible = true }.getInt(packet)
        }.getOrElse {
            packet.javaClass.methods.firstOrNull { it.name == "getSlotNum" }?.invoke(packet) as? Int
        } ?: return

        val player = event.player
        if (player.openInventory.topInventory.type != InventoryType.BREWING) return

        val cursor = player.itemOnCursor
        if (cursor == null || cursor.type.isAir) return

        if (slotNum == 3) {
            val recipe = WorkstationRecipes.getAll(BrewingRecipe::class.java)
                .firstOrNull { it.ingredient.matches(cursor) } ?: return
            event.isCancelled = true
            Bukkit.getScheduler().runTask(plugin, Runnable {
                val topInventory = player.openInventory.topInventory
                if (topInventory.type != InventoryType.BREWING) return@Runnable
                val toPlace = cursor.clone().apply { amount = 1 }
                topInventory.setItem(3, toPlace)
                if (cursor.amount <= 1) player.setItemOnCursor(null)
                else cursor.amount--
                player.updateInventory()
                val location = topInventory.location?.block?.location ?: return@Runnable
                scheduleBrew(location, recipe, player)
            })
        } else if (slotNum in 0..2) {
            val matches = WorkstationRecipes.getAll(BrewingRecipe::class.java)
                .any { it.base.matches(cursor) }
            if (!matches) return
            event.isCancelled = true
            Bukkit.getScheduler().runTask(plugin, Runnable {
                val topInventory = player.openInventory.topInventory
                if (topInventory.type != InventoryType.BREWING) return@Runnable
                val current = topInventory.getItem(slotNum)
                if (current != null && !current.type.isAir) return@Runnable
                val toPlace = cursor.clone().apply { amount = 1 }
                topInventory.setItem(slotNum, toPlace)
                if (cursor.amount <= 1) player.setItemOnCursor(null)
                else cursor.amount--
                player.updateInventory()
            })
        }
    }

    fun cancelBrew(location: Location) {
        pendingBrews.remove(location)?.cancel()
        progressTasks.remove(location)?.cancel()
    }

    private fun scheduleBrew(location: Location, recipe: BrewingRecipe, animPlayer: Player? = null) {
        cancelBrew(location)

        val brewTime = recipe.brewTime
        val player = animPlayer
        val containerId = if (player != null) getContainerId(player) else -1

        if (containerId >= 0 && player != null) {
            val totalSteps = (brewTime / 10).coerceAtLeast(1)
            var step = 0
            var progressTask: BukkitTask? = null
            progressTask = plugin.server.scheduler.runTaskTimer(plugin, Runnable {
                step++
                if (step > totalSteps || player.openInventory.topInventory.type != InventoryType.BREWING) {
                    progressTask?.cancel()
                    progressTasks.remove(location)
                    return@Runnable
                }
                val normalized = (400 * (totalSteps - step) / totalSteps).coerceAtLeast(0)
                sendBrewDataPacket(player, containerId, normalized)
            }, 0L, 10L)
            progressTasks[location] = progressTask!!
        }

        pendingBrews[location] = Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            pendingBrews.remove(location)
            progressTasks.remove(location)?.cancel()

            val state = location.block.state as? org.bukkit.block.BrewingStand ?: return@Runnable
            val brewer = state.inventory
            val ingredient = brewer.ingredient ?: return@Runnable
            if (!recipe.ingredient.matches(ingredient)) return@Runnable

            val matchedSlots = (0..2).filter { recipe.base.matches(brewer.getItem(it)) }
            if (matchedSlots.isEmpty()) return@Runnable

            val remainingIngredient = ingredient.clone()
            if (remainingIngredient.amount <= 1) brewer.ingredient = null
            else { remainingIngredient.amount--; brewer.ingredient = remainingIngredient }

            val item = recipe.output?.clone() ?: return@Runnable
            matchedSlots.forEach { brewer.setItem(it, item.clone()) }
            WorkstationRecipes.fireBrewCompleted(location, recipe, matchedSlots)
        }, brewTime.toLong())
    }

    // NMS helpers

    private fun getContainerId(player: Player): Int =
        runCatching {
            val nmsPlayer = player.javaClass.getMethod("getHandle").invoke(player)
            val menu = generateSequence(nmsPlayer.javaClass) { it.superclass }
                .flatMap { it.declaredFields.asSequence() }
                .first { it.name == "containerMenu" }
                .apply { isAccessible = true }
                .get(nmsPlayer)
            generateSequence(menu.javaClass) { it.superclass }
                .flatMap { it.declaredFields.asSequence() }
                .first { it.name == "containerId" }
                .apply { isAccessible = true }
                .getInt(menu)
        }.getOrDefault(-1)

    private fun sendBrewDataPacket(player: Player, containerId: Int, value: Int) {
        runCatching {
            val packetClass = Class.forName(
                "net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket"
            )
            val packet = packetClass
                .getDeclaredConstructor(Int::class.java, Int::class.java, Int::class.java)
                .newInstance(containerId, 0, value)
            val nmsPlayer = player.javaClass.getMethod("getHandle").invoke(player)
            val connection = generateSequence(nmsPlayer.javaClass) { it.superclass }
                .flatMap { it.declaredFields.asSequence() }
                .first { it.name == "connection" }
                .apply { isAccessible = true }
                .get(nmsPlayer)
            connection.javaClass.methods
                .first { it.name == "send" && it.parameterCount == 1 }
                .invoke(connection, packet)
        }
    }
}
