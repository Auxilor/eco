package com.willfp.eco.spigot.display;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.willfp.eco.core.display.Display;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.AbstractPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketSetSlot extends AbstractPacketAdapter {
    /**
     * Instantiate a new listener for {@link PacketType.Play.Server#SET_SLOT}.
     *
     * @param plugin The plugin to listen through.
     */
    public PacketSetSlot(@NotNull final EcoPlugin plugin) {
        super(plugin, PacketType.Play.Server.SET_SLOT, false);
    }

    @Override
    public void onSend(@NotNull final PacketContainer packet,
                       @NotNull final Player player) {
        packet.getItemModifier().modify(0, Display::display);
    }
}
