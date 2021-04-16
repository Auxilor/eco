package com.willfp.eco.internal.gui;

import com.willfp.eco.core.gui.menu.Menu;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class MenuHandler {
    private static final Map<Inventory, Menu> MENUS = new HashMap<>();

    public void registerMenu(@NotNull final Inventory inventory,
                             @NotNull final Menu menu) {
        MENUS.put(inventory, menu);
    }

    public void unregisterMenu(@NotNull final Inventory inventory) {
        MENUS.remove(inventory);
    }

    @Nullable
    public Menu getMenu(@NotNull final Inventory inventory) {
        return MENUS.get(inventory);
    }
}
