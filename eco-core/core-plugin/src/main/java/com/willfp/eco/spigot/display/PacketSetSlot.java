package com.willfp.eco.spigot.display;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.willfp.eco.core.AbstractPacketAdapter;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.display.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketSetSlot extends AbstractPacketAdapter {
    public PacketSetSlot(@NotNull final EcoPlugin plugin) {
        super(plugin, PacketType.Play.Server.SET_SLOT, false);
    }

    @Override
    public void onSend(@NotNull final PacketContainer packet,
                       @NotNull final Player player,
                       @NotNull final PacketEvent event) {
        packet.getItemModifier().modify(0, item -> Display.display(item, player));
    }
}
