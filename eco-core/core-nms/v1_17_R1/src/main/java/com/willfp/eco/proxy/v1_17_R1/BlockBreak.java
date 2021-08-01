package com.willfp.eco.proxy.v1_17_R1;

import com.willfp.eco.proxy.BlockBreakProxy;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class BlockBreak implements BlockBreakProxy {
    @Override
    public void breakBlock(@NotNull final Player player,
                           @NotNull final Block block) {
        player.breakBlock(block);
    }
}
