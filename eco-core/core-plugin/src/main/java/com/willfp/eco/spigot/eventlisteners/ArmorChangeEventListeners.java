package com.willfp.eco.spigot.eventlisteners;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.events.ArmorChangeEvent;
import com.willfp.eco.core.events.ArmorEquipEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArmorChangeEventListeners extends PluginDependent<EcoPlugin> implements Listener {
    /**
     * Pass an {@link EcoPlugin} in order to interface with it.
     *
     * @param plugin The plugin to manage.
     */
    public ArmorChangeEventListeners(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onArmorChange(@NotNull final ArmorEquipEvent event) {
        Player player = event.getPlayer();

        List<ItemStack> before = new ArrayList<>(Arrays.asList(player.getInventory().getArmorContents()));

        this.getPlugin().getScheduler().runLater(() -> {
            List<ItemStack> after = new ArrayList<>(Arrays.asList(player.getInventory().getArmorContents()));

            ArmorChangeEvent armorChangeEvent = new ArmorChangeEvent(player, before, after);
            Bukkit.getPluginManager().callEvent(armorChangeEvent);
        }, 1);
    }
}
