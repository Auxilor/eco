package com.willfp.eco.proxy.proxies;


import com.willfp.eco.util.proxy.AbstractProxy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PacketPlayOutRecipeUpdateFixProxy extends AbstractProxy {
    /**
     * Split recipe update packet into smaller packets.
     *
     * @param object The packet.
     * @param player The player.
     * @return The packets, split up.
     */
    List<Object> splitPackets(@NotNull Object object,
                              @NotNull Player player);
}
