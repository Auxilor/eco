package com.willfp.eco.spigot.integrations.multiverseinventories;

import com.onarandombox.multiverseinventories.event.WorldChangeShareHandlingEvent;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.events.ArmorChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiverseInventoriesEvent extends PluginDependent<EcoPlugin>  implements Listener {

    /**
     * Pass an {@link EcoPlugin} in order to interface with it.
     *
     * @param plugin The plugin to manage.
     */
    public MultiverseInventoriesEvent(@NotNull EcoPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onWorldChange(WorldChangeShareHandlingEvent event) {
        List<ItemStack> before = new ArrayList<>(Arrays.asList(event.getPlayer().getInventory().getArmorContents()));

        this.getPlugin().getScheduler().runLater(() -> {
            List<ItemStack> after = new ArrayList<>(Arrays.asList(event.getPlayer().getInventory().getArmorContents()));

            ArmorChangeEvent armorChangeEvent = new ArmorChangeEvent(event.getPlayer(), before, after);
            Bukkit.getPluginManager().callEvent(armorChangeEvent);
        }, 1);
    }

}
