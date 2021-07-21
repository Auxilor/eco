package com.willfp.eco.spigot.display;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.willfp.eco.core.AbstractPacketAdapter;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.display.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketSetCreativeSlot extends AbstractPacketAdapter {
    public PacketSetCreativeSlot(@NotNull final EcoPlugin plugin) {
        super(plugin, PacketType.Play.Client.SET_CREATIVE_SLOT, false);
    }

    @Override
    public void onReceive(@NotNull final PacketContainer packet,
                          @NotNull final Player player,
                          @NotNull final PacketEvent event) {
        packet.getItemModifier().modify(0, Display::revert);
    }
}
