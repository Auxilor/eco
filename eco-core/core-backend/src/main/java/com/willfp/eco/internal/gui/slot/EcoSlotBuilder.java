package com.willfp.eco.internal.gui.slot;

import com.willfp.eco.core.gui.slot.Slot;
import com.willfp.eco.core.gui.slot.SlotBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class EcoSlotBuilder implements SlotBuilder {
    private final Function<Player, ItemStack> provider;

    private BiConsumer<InventoryClickEvent, Slot> onLeftClick = null;

    private BiConsumer<InventoryClickEvent, Slot> onRightClick = null;

    private BiConsumer<InventoryClickEvent, Slot> onShiftLeftClick = null;

    private BiConsumer<InventoryClickEvent, Slot> onShiftRightClick = null;

    private BiConsumer<InventoryClickEvent, Slot> onMiddleClick = null;

    public EcoSlotBuilder(@NotNull final Function<Player, ItemStack> provider) {
        this.provider = provider;
    }

    public SlotBuilder onLeftClick(@NotNull final BiConsumer<InventoryClickEvent, Slot> action) {
        this.onLeftClick = action;
        return this;
    }

    public SlotBuilder onRightClick(@NotNull final BiConsumer<InventoryClickEvent, Slot> action) {
        this.onRightClick = action;
        return this;
    }

    public SlotBuilder onShiftLeftClick(@NotNull final BiConsumer<InventoryClickEvent, Slot> action) {
        this.onShiftLeftClick = action;
        return this;
    }

    public SlotBuilder onShiftRightClick(@NotNull final BiConsumer<InventoryClickEvent, Slot> action) {
        this.onShiftRightClick = action;
        return this;
    }

    public SlotBuilder onMiddleClick(@NotNull final BiConsumer<InventoryClickEvent, Slot> action) {
        this.onMiddleClick = action;
        return this;
    }

    public Slot build() {
        return new EcoSlot(provider, onLeftClick, onRightClick, onShiftLeftClick, onShiftRightClick, onMiddleClick);
    }
}
