package com.willfp.eco.spigot.integrations.antigrief;

import com.willfp.eco.core.integrations.antigrief.AntigriefWrapper;
import me.ulrich.lands.api.LandsAPI;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AntigriefUltimateLands implements AntigriefWrapper {
    @Override
    public boolean canBreakBlock(@NotNull final Player player,
                                 @NotNull final Block block) {
        return LandsAPI.getInstance().isOwnerOfChunk(player.getName(), block.getChunk()) || LandsAPI.getInstance().isMemberOfChunk(block.getChunk(), player);
    }

    @Override
    public boolean canCreateExplosion(@NotNull final Player player,
                                      @NotNull final Location location) {
        return LandsAPI.getInstance().isOwnerOfChunk(player.getName(), location.getChunk()) || LandsAPI.getInstance().isMemberOfChunk(location.getChunk(), player);
    }

    @Override
    public boolean canPlaceBlock(@NotNull final Player player,
                                 @NotNull final Block block) {
        return LandsAPI.getInstance().isOwnerOfChunk(player.getName(), block.getChunk()) || LandsAPI.getInstance().isMemberOfChunk(block.getChunk(), player);
    }

    @Override
    public boolean canInjure(@NotNull final Player player,
                             @NotNull final LivingEntity victim) {
        return LandsAPI.getInstance().isOwnerOfChunk(player.getName(), victim.getLocation().getChunk()) || LandsAPI.getInstance().isMemberOfChunk(victim.getLocation().getChunk(), player);
    }

    @Override
    public String getPluginName() {
        return "ULands";
    }
}
