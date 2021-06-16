package com.willfp.eco.spigot.eventlisteners;

import com.willfp.eco.core.events.ArmorEquipEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.jetbrains.annotations.NotNull;

public class DispenserArmorListener implements Listener {
    @EventHandler
    public void dispenseArmorEvent(@NotNull final BlockDispenseArmorEvent event) {
        ArmorType type = ArmorType.matchType(event.getItem());
        if (type != null && event.getTargetEntity() instanceof Player p) {
            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p);
            Bukkit.getPluginManager().callEvent(armorEquipEvent);
        }
    }
}
