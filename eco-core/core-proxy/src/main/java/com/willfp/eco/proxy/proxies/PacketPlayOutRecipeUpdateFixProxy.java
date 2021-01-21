package com.willfp.eco.proxy.proxies;


import com.willfp.eco.util.proxy.AbstractProxy;
import org.jetbrains.annotations.NotNull;

public interface PacketPlayOutRecipeUpdateFixProxy extends AbstractProxy {
    /**
     * Split recipe update packet into smaller packets.
     *
     * @param object The packet.
     * @return The extra packet for eco recipes.
     */
    Object splitAndModifyPacket(@NotNull Object object);
}
