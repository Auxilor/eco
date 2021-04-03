package com.willfp.eco.spigot.integrations.antigrief;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.willfp.eco.core.integrations.antigrief.AntigriefWrapper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AntigriefTowny implements AntigriefWrapper {
    @Override
    public boolean canBreakBlock(@NotNull final Player player,
                                 @NotNull final Block block) {
        TownyWorld world = TownyUniverse.getInstance().getWorldMap().get(block.getLocation().getWorld().getName());
        if (world == null) {
            return true;
        }
        if (TownyAPI.getInstance().isWilderness(block)) {
            return true;
        }
        return PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getType(), TownyPermission.ActionType.DESTROY)
                || PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getType(), TownyPermission.ActionType.BUILD);
    }

    @Override
    public boolean canCreateExplosion(@NotNull final Player player,
                                      @NotNull final Location location) {
        TownyWorld world = TownyUniverse.getInstance().getWorldMap().get(location.getWorld().getName());
        if (world == null) {
            return true;
        }
        if (TownyAPI.getInstance().isWilderness(location)) {
            return true;
        }
        return PlayerCacheUtil.getCachePermission(player, location, Material.TNT, TownyPermission.ActionType.ITEM_USE);
    }

    @Override
    public boolean canPlaceBlock(@NotNull final Player player,
                                 @NotNull final Block block) {
        TownyWorld world = TownyUniverse.getInstance().getWorldMap().get(block.getLocation().getWorld().getName());
        if (world == null) {
            return true;
        }
        if (TownyAPI.getInstance().isWilderness(block)) {
            return true;
        }
        return PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getType(), TownyPermission.ActionType.BUILD);
    }

    @Override
    public boolean canInjure(@NotNull final Player player,
                             @NotNull final LivingEntity victim) {
        TownyWorld world = TownyUniverse.getInstance().getWorldMap().get(victim.getLocation().getWorld().getName());
        if (world == null) {
            return true;
        }
        if (TownyAPI.getInstance().isWilderness(victim.getLocation())) {
            if (victim instanceof Player) {
                return world.isPVP();
            } else {
                return true;
            }
        }
        if (victim instanceof Player) {
            try {
                Town town = WorldCoord.parseWorldCoord(victim.getLocation()).getTownBlock().getTown();
                return town.isPVP();
            } catch (Exception ignored) {
                // If exception, no town was found, thus return true.
            }
        } else {
            try {
                Town town = WorldCoord.parseWorldCoord(victim.getLocation()).getTownBlock().getTown();
                return town.hasMobs();
            } catch (Exception ignored) {
                // If exception, no town was found, thus return true.
            }
        }
        return true;
    }

    @Override
    public String getPluginName() {
        return "Towny";
    }
}
