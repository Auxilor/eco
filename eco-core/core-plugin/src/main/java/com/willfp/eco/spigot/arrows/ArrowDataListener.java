package com.willfp.eco.spigot.arrows;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ArrowDataListener extends PluginDependent<EcoPlugin> implements Listener {
    public ArrowDataListener(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onLaunch(final ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) {
            return;
        }

        if (!(arrow.getShooter() instanceof LivingEntity entity)) {
            return;
        }

        if (entity.getEquipment() == null) {
            return;
        }

        ItemStack item = entity.getEquipment().getItemInMainHand();

        if (item.getType().equals(Material.AIR) || !item.hasItemMeta() || item.getItemMeta() == null) {
            return;
        }

        arrow.setMetadata("shot-from", this.getPlugin().getMetadataValueFactory().create(item));
    }
}
