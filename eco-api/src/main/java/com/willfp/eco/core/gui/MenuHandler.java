package com.willfp.eco.core.gui;

import com.willfp.eco.core.gui.menu.Menu;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class MenuHandler {
    /**
     * All registered menus.
     */
    private static final Map<Inventory, Menu> MENUS = new HashMap<>();

    /**
     * Register a menu.
     *
     * @param inventory The inventory corresponding to the menu.
     * @param menu      The menu.
     */
    public void registerMenu(@NotNull final Inventory inventory,
                             @NotNull final Menu menu) {
        MENUS.put(inventory, menu);
    }

    /**
     * Unregister a menu.
     *
     * @param inventory The inventory for the menu.
     */
    public void unregisterMenu(@NotNull final Inventory inventory) {
        MENUS.remove(inventory);
    }

    /**
     * Get a menu from an inventory.
     *
     * @param inventory The inventory.
     * @return The menu, or null if not a menu.
     */
    @Nullable
    public Menu getMenu(@NotNull final Inventory inventory) {
        return MENUS.get(inventory);
    }
}
