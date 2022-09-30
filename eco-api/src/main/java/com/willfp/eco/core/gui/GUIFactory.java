package com.willfp.eco.core.gui;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.core.gui.menu.MenuBuilder;
import com.willfp.eco.core.gui.menu.MenuType;
import com.willfp.eco.core.gui.slot.SlotBuilder;
import com.willfp.eco.core.gui.slot.functional.SlotProvider;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Internal component used by {@link com.willfp.eco.core.gui.menu.Menu#builder(int)}
 * and {@link com.willfp.eco.core.gui.slot.Slot#builder(ItemStack)}.
 */
@ApiStatus.Internal
@Eco.HandlerComponent
public interface GUIFactory {
    /**
     * Create slot builder.
     *
     * @param provider The provider.
     * @return The builder.
     */
    @NotNull
    SlotBuilder createSlotBuilder(@NotNull SlotProvider provider);

    /**
     * Create menu builder.
     *
     * @param rows The amount of rows.
     * @param type The type.
     * @return The builder.
     */
    @NotNull
    MenuBuilder createMenuBuilder(int rows,
                                  @NotNull MenuType type);

    /**
     * Combine the state of two menus together.
     *
     * @param base       The base menu.
     * @param additional The additional state.
     * @return The menu.
     */
    @NotNull
    Menu blendMenuState(@NotNull Menu base,
                        @NotNull Menu additional);
}
