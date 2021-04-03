package com.willfp.eco.spigot.display;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.willfp.eco.proxy.proxies.ChatComponentProxy;
import com.willfp.eco.spigot.InternalProxyUtils;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.AbstractPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketChat extends AbstractPacketAdapter {
    /**
     * Instantiate a new listener for {@link PacketType.Play.Server#CHAT}.
     *
     * @param plugin The plugin to listen through.
     */
    public PacketChat(@NotNull final EcoPlugin plugin) {
        super(plugin, PacketType.Play.Server.CHAT, ListenerPriority.MONITOR, true);
    }

    @Override
    public void onSend(@NotNull final PacketContainer packet,
                       @NotNull final Player player) {
        for (int i = 0; i < packet.getChatComponents().size(); i++) {
            WrappedChatComponent component = packet.getChatComponents().read(i);
            if (component == null) {
                continue;
            }

            if (component.getHandle() == null) {
                return;
            }

            WrappedChatComponent newComponent = WrappedChatComponent.fromHandle(InternalProxyUtils.getProxy(ChatComponentProxy.class).modifyComponent(component.getHandle()));
            packet.getChatComponents().write(i, newComponent);
        }
    }
}
