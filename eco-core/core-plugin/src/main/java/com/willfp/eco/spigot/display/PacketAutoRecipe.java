package com.willfp.eco.spigot.display;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.willfp.eco.proxy.proxies.AutoCraftProxy;
import com.willfp.eco.spigot.InternalProxyUtils;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.AbstractPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class PacketAutoRecipe extends AbstractPacketAdapter {
    /**
     * Instantiate a new listener for {@link PacketType.Play.Server#SET_SLOT}.
     *
     * @param plugin The plugin to listen through.
     */
    public PacketAutoRecipe(@NotNull final EcoPlugin plugin) {
        super(plugin, PacketType.Play.Server.AUTO_RECIPE, false);
    }

    @Override
    public void onSend(@NotNull final PacketContainer packet,
                       @NotNull final Player player) {
        if (!EcoPlugin.LOADED_ECO_PLUGINS.contains(packet.getMinecraftKeys().getValues().get(0).getFullKey().split(":")[0])) {
            return;
        }

        if (packet.getMinecraftKeys().getValues().get(0).getFullKey().split(":")[1].contains("displayed")) {
            return;
        }

        try {
            InternalProxyUtils.getProxy(AutoCraftProxy.class).modifyPacket(packet.getHandle());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        PacketContainer newAutoRecipe = new PacketContainer(PacketType.Play.Server.AUTO_RECIPE);
        newAutoRecipe.getMinecraftKeys().write(0, packet.getMinecraftKeys().read(0));

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, newAutoRecipe);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
