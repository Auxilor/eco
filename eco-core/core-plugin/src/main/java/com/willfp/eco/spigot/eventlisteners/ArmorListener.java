package com.willfp.eco.spigot.eventlisteners;

import com.willfp.eco.core.events.ArmorEquipEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArmorListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public final void inventoryClick(@NotNull final InventoryClickEvent event) {
        boolean shift = false;
        boolean numberkey = false;
        if (event.isCancelled()) {
            return;
        }
        if (event.getAction() == InventoryAction.NOTHING) {
            return;
        }
        if (event.getClick().equals(ClickType.SHIFT_LEFT) || event.getClick().equals(ClickType.SHIFT_RIGHT)) {
            shift = true;
        }
        if (event.getClick().equals(ClickType.NUMBER_KEY)) {
            numberkey = true;
        }
        if (event.getSlotType() != SlotType.ARMOR && event.getSlotType() != SlotType.QUICKBAR && event.getSlotType() != SlotType.CONTAINER) {
            return;
        }
        if (event.getClickedInventory() != null && !event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
            return;
        }
        if (!event.getInventory().getType().equals(InventoryType.CRAFTING) && !event.getInventory().getType().equals(InventoryType.PLAYER)) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        ArmorType newArmorType = ArmorType.matchType(shift ? event.getCurrentItem() : event.getCursor());
        if (!shift && newArmorType != null && event.getRawSlot() != newArmorType.getSlot()) {
            // Used for drag and drop checking to make sure you aren't trying to place a helmet in the boots slot.
            return;
        }
        if (shift) {
            newArmorType = ArmorType.matchType(event.getCurrentItem());
            if (newArmorType != null) {
                boolean equipping = true;
                if (event.getRawSlot() == newArmorType.getSlot()) {
                    equipping = false;
                }
                if (newArmorType.equals(ArmorType.HELMET)
                        && (equipping == isAirOrNull(event.getWhoClicked().getInventory().getHelmet()))
                        || newArmorType.equals(ArmorType.CHESTPLATE)
                        && (equipping == isAirOrNull(event.getWhoClicked().getInventory().getChestplate()))
                        || newArmorType.equals(ArmorType.LEGGINGS)
                        && (equipping == isAirOrNull(event.getWhoClicked().getInventory().getLeggings()))
                        || newArmorType.equals(ArmorType.BOOTS)
                        && (equipping == isAirOrNull(event.getWhoClicked().getInventory().getBoots()))) {
                    ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(
                            (Player) event.getWhoClicked()
                    );
                    Bukkit.getPluginManager().callEvent(armorEquipEvent);
                }
            }
        } else {
            if (numberkey) {
                if (event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
                    ItemStack hotbarItem = event.getClickedInventory().getItem(event.getHotbarButton());
                    if (!isAirOrNull(hotbarItem)) {
                        newArmorType = ArmorType.matchType(hotbarItem);
                    } else {
                        newArmorType = ArmorType.matchType(!isAirOrNull(event.getCurrentItem()) ? event.getCurrentItem() : event.getCursor());
                    }
                }
            } else {
                if (isAirOrNull(event.getCursor()) && !isAirOrNull(event.getCurrentItem())) {
                    newArmorType = ArmorType.matchType(event.getCurrentItem());
                }
            }
            if (newArmorType != null && event.getRawSlot() == newArmorType.getSlot()) {
                ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) event.getWhoClicked());
                Bukkit.getPluginManager().callEvent(armorEquipEvent);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(@NotNull final PlayerInteractEvent e) {
        if (e.useItemInHand().equals(Result.DENY)) {
            return;
        }
        if (e.getAction() == Action.PHYSICAL) {
            return;
        }
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            if (!e.useInteractedBlock().equals(Result.DENY)) {
                if (e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK && !player.isSneaking()) {
                    Material mat = e.getClickedBlock().getType();
                }
            }
            ArmorType newArmorType = ArmorType.matchType(e.getItem());
            if (newArmorType != null) {
                if (newArmorType.equals(ArmorType.HELMET)
                        && isAirOrNull(e.getPlayer().getInventory().getHelmet())
                        || newArmorType.equals(ArmorType.CHESTPLATE)
                        && isAirOrNull(e.getPlayer().getInventory().getChestplate())
                        || newArmorType.equals(ArmorType.LEGGINGS)
                        && isAirOrNull(e.getPlayer().getInventory().getLeggings())
                        || newArmorType.equals(ArmorType.BOOTS)
                        && isAirOrNull(e.getPlayer().getInventory().getBoots())) {
                    ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(e.getPlayer());
                    Bukkit.getPluginManager().callEvent(armorEquipEvent);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void inventoryDrag(@NotNull final InventoryDragEvent event) {
        ArmorType type = ArmorType.matchType(event.getOldCursor());
        if (event.getRawSlots().isEmpty()) {
            return;
        }
        if (type != null && type.getSlot() == event.getRawSlots().stream().findFirst().orElse(0)) {
            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) event.getWhoClicked());
            Bukkit.getPluginManager().callEvent(armorEquipEvent);
        }
    }

    @EventHandler
    public void playerJoinEvent(@NotNull final PlayerJoinEvent event) {
        ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(event.getPlayer());
        Bukkit.getPluginManager().callEvent(armorEquipEvent);
    }

    @EventHandler
    public void playerRespawnEvent(@NotNull final PlayerRespawnEvent event) {
        ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(event.getPlayer());
        Bukkit.getPluginManager().callEvent(armorEquipEvent);
    }

    @EventHandler
    public void itemBreakEvent(@NotNull final PlayerItemBreakEvent event) {
        ArmorType type = ArmorType.matchType(event.getBrokenItem());
        if (type != null) {
            Player p = event.getPlayer();
            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p);
            Bukkit.getPluginManager().callEvent(armorEquipEvent);
        }
    }

    @EventHandler
    public void playerDeathEvent(@NotNull final PlayerDeathEvent event) {
        Player p = event.getEntity();
        if (event.getKeepInventory()) {
            return;
        }
        for (ItemStack i : p.getInventory().getArmorContents()) {
            if (!isAirOrNull(i)) {
                Bukkit.getPluginManager().callEvent(new ArmorEquipEvent(p));
            }
        }
    }

    public static boolean isAirOrNull(@Nullable final ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }
}
