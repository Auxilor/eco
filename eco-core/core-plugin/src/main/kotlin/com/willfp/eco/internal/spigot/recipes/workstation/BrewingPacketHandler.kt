package com.willfp.eco.internal.spigot.recipes.workstation

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.core.recipe.workstation.BrewingRecipe
import com.willfp.eco.core.recipe.workstation.WorkstationRecipes
import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.eco.internal.spigot.proxies.WorkstationPacketProxy
import org.bukkit.Location
import org.bukkit.block.BrewingStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import java.util.concurrent.ConcurrentHashMap

class BrewingPacketHandler(private val plugin: EcoPlugin) : PacketListener, Listener {

    private val pendingBrews = ConcurrentHashMap<Location, EcoTask>()
    private val progressTasks = ConcurrentHashMap<Location, EcoTask>()

    init {
        WorkstationRecipes.registerBrewCancelHook { cancelBrew(it) }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onShiftClickIngredient(event: InventoryClickEvent) {
        if (event.inventory.type != InventoryType.BREWING) return
        if (!event.isShiftClick) return
        val player = event.whoClicked as? Player ?: return
        val location = event.inventory.location ?: return
        if (pendingBrews.containsKey(location)) return
        plugin.scheduler.at(location).run {
            val brewer = (location.block.state as? BrewingStand)?.inventory ?: return@run
            val ingredient = brewer.ingredient ?: return@run
            val recipe = WorkstationRecipes.getAll(BrewingRecipe::class.java)
                .firstOrNull {
                    it.ingredient.matches(ingredient) &&
                    (0..2).any { slot -> it.base.matches(brewer.getItem(slot)) }
                } ?: return@run
            scheduleBrew(location, recipe, player)
        }
    }

    override fun onReceive(event: PacketEvent) {
        val slotNum = plugin.getProxy(WorkstationPacketProxy::class.java)
            .getContainerClickSlot(event.packet) ?: return

        val player = event.player
        if (player.openInventory.topInventory.type != InventoryType.BREWING) return

        val cursor = player.itemOnCursor
        if (cursor == null || cursor.type.isAir) return

        if (slotNum == 3) {
            val recipe = WorkstationRecipes.getAll(BrewingRecipe::class.java)
                .firstOrNull { it.ingredient.matches(cursor) } ?: return
            event.isCancelled = true
            plugin.scheduler.on(player).run {
                val topInventory = player.openInventory.topInventory
                if (topInventory.type != InventoryType.BREWING) return@run
                val toPlace = cursor.clone().apply { amount = 1 }
                topInventory.setItem(3, toPlace)
                if (cursor.amount <= 1) player.setItemOnCursor(null)
                else cursor.amount--
                player.updateInventory()
                val location = topInventory.location?.block?.location ?: return@run
                scheduleBrew(location, recipe, player)
            }
        } else if (slotNum in 0..2) {
            val matches = WorkstationRecipes.getAll(BrewingRecipe::class.java)
                .any { it.base.matches(cursor) }
            if (!matches) return
            event.isCancelled = true
            plugin.scheduler.on(player).run {
                val topInventory = player.openInventory.topInventory
                if (topInventory.type != InventoryType.BREWING) return@run
                val current = topInventory.getItem(slotNum)
                if (current != null && !current.type.isAir) return@run
                val toPlace = cursor.clone().apply { amount = 1 }
                topInventory.setItem(slotNum, toPlace)
                if (cursor.amount <= 1) player.setItemOnCursor(null)
                else cursor.amount--
                player.updateInventory()
            }
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
        val nmsPacket = plugin.getProxy(WorkstationPacketProxy::class.java)
        val containerId = if (player != null) nmsPacket.getOpenContainerId(player) else -1

        if (containerId >= 0 && player != null) {
            val totalSteps = (brewTime / 10).coerceAtLeast(1)
            var step = 0
            var progressTask: EcoTask? = null
            progressTask = plugin.scheduler.at(location).runTimer(0L, 10L) {
                step++
                if (step > totalSteps || player.openInventory.topInventory.type != InventoryType.BREWING) {
                    progressTask?.cancel()
                    progressTasks.remove(location)
                    return@runTimer
                }
                val normalized = (400 * (totalSteps - step) / totalSteps).coerceAtLeast(0)
                nmsPacket.sendContainerDataPacket(player, containerId, normalized)
            }
            progressTasks[location] = progressTask!!
        }

        pendingBrews[location] = plugin.scheduler.at(location).runLater(brewTime.toLong()) {
            pendingBrews.remove(location)
            progressTasks.remove(location)?.cancel()

            val state = location.block.state as? BrewingStand ?: return@runLater
            val brewer = state.inventory
            val ingredient = brewer.ingredient ?: return@runLater
            if (!recipe.ingredient.matches(ingredient)) return@runLater

            val matchedSlots = (0..2).filter { recipe.base.matches(brewer.getItem(it)) }
            if (matchedSlots.isEmpty()) return@runLater

            val remainingIngredient = ingredient.clone()
            if (remainingIngredient.amount <= 1) brewer.ingredient = null
            else { remainingIngredient.amount--; brewer.ingredient = remainingIngredient }

            val item = recipe.output?.clone() ?: return@runLater
            matchedSlots.forEach { brewer.setItem(it, item.clone()) }
            WorkstationRecipes.fireBrewCompleted(location, recipe, matchedSlots)
        }
    }

}
