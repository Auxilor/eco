package com.willfp.eco.spigot.display;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.willfp.eco.core.display.Display;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.AbstractPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketSetCreativeSlot extends AbstractPacketAdapter {
    /**
     * Instantiate a new listener for {@link PacketType.Play.Client#SET_CREATIVE_SLOT}.
     *
     * @param plugin The plugin to listen through.
     */
    public PacketSetCreativeSlot(@NotNull final EcoPlugin plugin) {
        super(plugin, PacketType.Play.Client.SET_CREATIVE_SLOT, false);
    }

    @Override
    public void onReceive(@NotNull final PacketContainer packet,
                          @NotNull final Player player) {
        packet.getItemModifier().modify(0, Display::revert);
    }
}
