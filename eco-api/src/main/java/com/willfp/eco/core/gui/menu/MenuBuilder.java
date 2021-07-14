package com.willfp.eco.core.gui.menu;

import com.willfp.eco.core.gui.slot.FillerMask;
import com.willfp.eco.core.gui.slot.Slot;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface MenuBuilder {
    MenuBuilder setTitle(@NotNull String title);

    MenuBuilder setSlot(int row,
                         int column,
                         @NotNull Slot slot);

    MenuBuilder setMask(@NotNull FillerMask mask);

    MenuBuilder onClose(@NotNull Consumer<InventoryCloseEvent> action);

    Menu build();
}
