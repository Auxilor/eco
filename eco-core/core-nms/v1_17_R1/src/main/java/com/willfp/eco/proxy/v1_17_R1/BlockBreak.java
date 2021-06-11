package com.willfp.eco.proxy.v1_17_R1;

import com.willfp.eco.proxy.proxies.BlockBreakProxy;
import net.minecraft.core.BlockPosition;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class BlockBreak implements BlockBreakProxy {
    @Override
    public void breakBlock(@NotNull final Player player,
                           @NotNull final Block block) {
        ((CraftPlayer) player).getHandle().d.breakBlock(new BlockPosition(block.getX(), block.getY(), block.getZ()));
    }
}
