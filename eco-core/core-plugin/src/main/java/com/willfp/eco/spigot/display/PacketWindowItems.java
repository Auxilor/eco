package com.willfp.eco.spigot.display;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.willfp.eco.core.display.Display;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.AbstractPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketWindowItems extends AbstractPacketAdapter {
    /**
     * Instantiate a new listener for {@link PacketType.Play.Server#WINDOW_ITEMS}.
     *
     * @param plugin The plugin to listen through.
     */
    public PacketWindowItems(@NotNull final EcoPlugin plugin) {
        super(plugin, PacketType.Play.Server.WINDOW_ITEMS, false);
    }

    @Override
    public void onSend(@NotNull final PacketContainer packet,
                       @NotNull final Player player) {
        packet.getItemListModifier().modify(0, itemStacks -> {
            if (itemStacks == null) {
                return null;
            }
            itemStacks.forEach(Display::display);
            return itemStacks;
        });
    }
}
