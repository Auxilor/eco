package com.willfp.eco.internal.spigot.eventlisteners.armor

import com.willfp.eco.core.events.ArmorEquipEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack

class ArmorListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun inventoryClick(event: InventoryClickEvent) {
        var shift = false
        var numberkey = false
        if (event.isCancelled) {
            return
        }
        if (event.action == InventoryAction.NOTHING) {
            return
        }
        if (event.click == ClickType.SHIFT_LEFT || event.click == ClickType.SHIFT_RIGHT) {
            shift = true
        }
        if (event.click == ClickType.NUMBER_KEY) {
            numberkey = true
        }
        if (event.slotType != InventoryType.SlotType.ARMOR && event.slotType != InventoryType.SlotType.QUICKBAR && event.slotType != InventoryType.SlotType.CONTAINER) {
            return
        }
        if (event.clickedInventory != null && event.clickedInventory!!.type != InventoryType.PLAYER) {
            return
        }
        if (event.inventory.type != InventoryType.CRAFTING && event.inventory.type != InventoryType.PLAYER) {
            return
        }
        if (event.whoClicked !is Player) {
            return
        }
        var newArmorType = ArmorType.matchType(if (shift) event.currentItem else event.cursor)
        if (!shift && newArmorType != null && event.rawSlot != newArmorType.slot) {
            // Used for drag and drop checking to make sure you aren't trying to place a helmet in the boots slot.
            return
        }
        if (shift) {
            newArmorType = ArmorType.matchType(event.currentItem)
            if (newArmorType != null) {
                var equipping = true
                if (event.rawSlot == newArmorType.slot) {
                    equipping = false
                }
                if (newArmorType == ArmorType.HELMET && equipping == isAirOrNull(event.whoClicked.inventory.helmet) || newArmorType == ArmorType.CHESTPLATE && equipping == isAirOrNull(
                        event.whoClicked.inventory.chestplate
                    ) || newArmorType == ArmorType.LEGGINGS && equipping == isAirOrNull(event.whoClicked.inventory.leggings) || newArmorType == ArmorType.BOOTS && equipping == isAirOrNull(
                        event.whoClicked.inventory.boots
                    )
                ) {
                    val armorEquipEvent = ArmorEquipEvent(
                        (event.whoClicked as Player)
                    )
                    Bukkit.getPluginManager().callEvent(armorEquipEvent)
                }
            }
        } else {
            if (numberkey) {
                if (event.clickedInventory!!.type == InventoryType.PLAYER) {
                    val hotbarItem = event.clickedInventory!!.getItem(event.hotbarButton)
                    newArmorType = if (!isAirOrNull(hotbarItem)) {
                        ArmorType.matchType(hotbarItem)
                    } else {
                        ArmorType.matchType(if (!isAirOrNull(event.currentItem)) event.currentItem else event.cursor)
                    }
                }
            } else {
                if (isAirOrNull(event.cursor) && !isAirOrNull(event.currentItem)) {
                    newArmorType = ArmorType.matchType(event.currentItem)
                }
            }
            if (newArmorType != null && event.rawSlot == newArmorType.slot) {
                val armorEquipEvent = ArmorEquipEvent(
                    (event.whoClicked as Player)
                )
                Bukkit.getPluginManager().callEvent(armorEquipEvent)
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun playerInteractEvent(e: PlayerInteractEvent) {
        if (e.useItemInHand() == Event.Result.DENY) {
            return
        }
        if (e.action == Action.PHYSICAL) {
            return
        }
        if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK) {
            val newArmorType = ArmorType.matchType(e.item)
            if (newArmorType != null) {
                if (newArmorType == ArmorType.HELMET && isAirOrNull(e.player.inventory.helmet) || newArmorType == ArmorType.CHESTPLATE && isAirOrNull(
                        e.player.inventory.chestplate
                    ) || newArmorType == ArmorType.LEGGINGS && isAirOrNull(e.player.inventory.leggings) || newArmorType == ArmorType.BOOTS && isAirOrNull(
                        e.player.inventory.boots
                    )
                ) {
                    val armorEquipEvent = ArmorEquipEvent(e.player)
                    Bukkit.getPluginManager().callEvent(armorEquipEvent)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun inventoryDrag(event: InventoryDragEvent) {
        val type = ArmorType.matchType(event.oldCursor)
        if (event.rawSlots.isEmpty()) {
            return
        }
        if (type != null && type.slot == event.rawSlots.stream().findFirst().orElse(0)) {
            val armorEquipEvent = ArmorEquipEvent(
                (event.whoClicked as Player)
            )
            Bukkit.getPluginManager().callEvent(armorEquipEvent)
        }
    }

    @EventHandler
    fun playerJoinEvent(event: PlayerJoinEvent) {
        val armorEquipEvent = ArmorEquipEvent(event.player)
        Bukkit.getPluginManager().callEvent(armorEquipEvent)
    }

    @EventHandler
    fun playerRespawnEvent(event: PlayerRespawnEvent) {
        val armorEquipEvent = ArmorEquipEvent(event.player)
        Bukkit.getPluginManager().callEvent(armorEquipEvent)
    }

    @EventHandler
    fun itemBreakEvent(event: PlayerItemBreakEvent) {
        val type = ArmorType.matchType(event.brokenItem)
        if (type != null) {
            val p = event.player
            val armorEquipEvent = ArmorEquipEvent(p)
            Bukkit.getPluginManager().callEvent(armorEquipEvent)
        }
    }

    @EventHandler
    fun playerDeathEvent(event: PlayerDeathEvent) {
        val p = event.entity
        if (event.keepInventory) {
            return
        }
        for (i in p.inventory.armorContents) {
            if (!isAirOrNull(i)) {
                Bukkit.getPluginManager().callEvent(ArmorEquipEvent(p))
            }
        }
    }

    companion object {
        fun isAirOrNull(item: ItemStack?): Boolean {
            return item == null || item.type == Material.AIR
        }
    }
}