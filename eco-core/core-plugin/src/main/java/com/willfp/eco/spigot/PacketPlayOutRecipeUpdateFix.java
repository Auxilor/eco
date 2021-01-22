package com.willfp.eco.spigot;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.willfp.eco.proxy.proxies.PacketPlayOutRecipeUpdateFixProxy;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class PacketPlayOutRecipeUpdateFix extends PacketAdapter {
    /**
     * Create new fixer for PacketPlayOutRecipeUpdate.
     *
     * @param plugin Plugin.
     */
    public PacketPlayOutRecipeUpdateFix(@NotNull final AbstractEcoPlugin plugin) {
        super(plugin, PacketType.Play.Server.RECIPE_UPDATE);
    }

    @Override
    public void onPacketSending(@NotNull final PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();

        List<Object> packets = InternalProxyUtils.getProxy(PacketPlayOutRecipeUpdateFixProxy.class).splitPackets(packet.getHandle(), player);
        if (packets.size() > 1) {
            event.setCancelled(true);
            for (Object o : packets) {
                PacketContainer container = PacketContainer.fromPacket(o);
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, container);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
