package com.willfp.eco.spigot.display;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.willfp.eco.util.display.Display;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.eco.util.protocollib.AbstractPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketSetCreativeSlot extends AbstractPacketAdapter {
    /**
     * Instantiate a new listener for {@link PacketType.Play.Client#SET_CREATIVE_SLOT}.
     *
     * @param plugin The plugin to listen through.
     */
    public PacketSetCreativeSlot(@NotNull final AbstractEcoPlugin plugin) {
        super(plugin, PacketType.Play.Client.SET_CREATIVE_SLOT, false);
    }

    @Override
    public void onReceive(@NotNull final PacketContainer packet,
                          @NotNull final Player player) {
        packet.getItemModifier().modify(0, Display::revert);
    }
}
