package com.willfp.eco.spigot.gui;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.internal.gui.EcoMenu;
import com.willfp.eco.internal.gui.EcoSlot;
import com.willfp.eco.internal.gui.MenuHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

public class GUIListener extends PluginDependent implements Listener {
    /**
     * Pass an {@link EcoPlugin} in order to interface with it.
     *
     * @param plugin The plugin to manage.
     */
    public GUIListener(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void handleSlotClick(@NotNull final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (event.getClickedInventory() == null) {
            return;
        }
        Menu menu = MenuHandler.getMenu(event.getClickedInventory());
        if (menu == null) {
            return;
        }

        int row = Math.floorDiv(event.getSlot(), 9);
        int column = event.getSlot() - (row * 9);

        EcoSlot slot = (EcoSlot) menu.getSlot(row, column);
        event.setCancelled(true);
        slot.handleInventoryClick(event);
    }

    @EventHandler
    public void handleClose(@NotNull final InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        EcoMenu menu = (EcoMenu) MenuHandler.getMenu(event.getInventory());
        if (menu == null) {
            return;
        }

        menu.handleClose(event);

        this.getPlugin().getScheduler().run(() -> MenuHandler.unregisterMenu(event.getInventory()));
    }
}
