package com.willfp.eco.proxy;

import com.willfp.eco.core.proxy.AbstractProxy;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface BlockBreakProxy extends AbstractProxy {
    void breakBlock(@NotNull Player player,
                    @NotNull Block block);
}
