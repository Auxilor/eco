package com.willfp.eco.proxy;


import com.willfp.eco.core.proxy.AbstractProxy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ChatComponentProxy extends AbstractProxy {
    Object modifyComponent(@NotNull Object object,
                           @NotNull Player player);
}
