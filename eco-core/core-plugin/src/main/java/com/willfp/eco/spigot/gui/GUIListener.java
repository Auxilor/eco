package com.willfp.eco.spigot.gui;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.core.gui.slot.Slot;
import com.willfp.eco.internal.gui.menu.EcoMenu;
import com.willfp.eco.internal.gui.menu.MenuHandler;
import com.willfp.eco.internal.gui.slot.EcoSlot;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

public class GUIListener extends PluginDependent<EcoPlugin> implements Listener {
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

        Slot slot = menu.getSlot(row, column);

        Validate.isTrue(slot instanceof EcoSlot, "Slot not instance of EcoSlot!");

        EcoSlot ecoSlot = (EcoSlot) menu.getSlot(row, column);
        event.setCancelled(true);
        ecoSlot.handleInventoryClick(event);
    }

    @EventHandler
    public void handleClose(@NotNull final InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Menu menu = MenuHandler.getMenu(event.getInventory());

        Validate.isTrue(menu instanceof EcoMenu, "Menu not instance of EcoMenu!");

        EcoMenu ecoMenu = (EcoMenu) menu;

        ecoMenu.handleClose(event);

        this.getPlugin().getScheduler().run(() -> MenuHandler.unregisterMenu(event.getInventory()));
    }
}
