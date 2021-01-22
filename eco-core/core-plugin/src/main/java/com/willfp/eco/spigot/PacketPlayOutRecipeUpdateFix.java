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

        Object otherPacket = InternalProxyUtils.getProxy(PacketPlayOutRecipeUpdateFixProxy.class).splitAndModifyPacket(packet.getHandle());
        if (otherPacket != null) {
            event.setCancelled(true);
            PacketContainer container = PacketContainer.fromPacket(otherPacket);
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, container);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
