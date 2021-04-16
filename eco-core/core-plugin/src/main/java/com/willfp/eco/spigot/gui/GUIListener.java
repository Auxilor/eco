package com.willfp.eco.spigot.gui;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.gui.menu.FillerMask;
import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.core.gui.slot.Slot;
import com.willfp.eco.internal.gui.EcoMenu;
import com.willfp.eco.internal.gui.EcoSlot;
import com.willfp.eco.internal.gui.MenuHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
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

    @EventHandler
    public void test(@NotNull final AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        if (!message.equals("guitest")) {
            return;
        }

        this.getPlugin().getScheduler().run(() -> {
            Menu.builder(5)
                    .setMask(new FillerMask(
                            Material.BLACK_STAINED_GLASS_PANE,
                            "111111111",
                            "100000001",
                            "100000001",
                            "100000001",
                            "111111111"
                    ))
                    .setSlot(1, 3, Slot.builder(new ItemStack(Material.TNT))
                            .onLeftClick((event1, slot) -> event1.getWhoClicked().sendMessage("CLICK"))
                            .build())
                    .setTitle("Poggers")
                    .onClose(event1 -> event1.getPlayer().sendMessage("CLOSED"))
                    .build()
                    .open(event.getPlayer());
        });
    }
}
