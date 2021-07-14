package com.willfp.eco.proxy.v1_16_R2;

import com.willfp.eco.proxy.BlockBreakProxy;
import net.minecraft.server.v1_16_R2.BlockPosition;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class BlockBreak implements BlockBreakProxy {
    @Override
    public void breakBlock(@NotNull final Player player,
                           @NotNull final Block block) {
        if (!(player instanceof CraftPlayer)) {
            return;
        }

        ((CraftPlayer) player).getHandle().playerInteractManager.breakBlock(new BlockPosition(block.getX(), block.getY(), block.getZ()));
    }
}
