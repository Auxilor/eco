package com.willfp.eco.spigot.integrations.antigrief;

import com.willfp.eco.core.integrations.antigrief.AntigriefWrapper;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.kingdoms.constants.kingdom.Kingdom;
import org.kingdoms.constants.kingdom.model.KingdomRelation;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.player.DefaultKingdomPermission;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.managers.PvPManager;

public class AntigriefKingdoms implements AntigriefWrapper {
    @Override
    public boolean canBreakBlock(@NotNull final Player player,
                                 @NotNull final Block block) {
        KingdomPlayer kp = KingdomPlayer.getKingdomPlayer(player);
        if (kp.isAdmin()) {
            return true;
        }

        Kingdom kingdom = kp.getKingdom();
        if (kingdom == null) {
            return false;
        }

        Land land = Land.getLand(block);

        if (land == null) {
            return true;
        }

        DefaultKingdomPermission permission = land.isNexusLand() ? DefaultKingdomPermission.NEXUS_BUILD : DefaultKingdomPermission.BUILD;
        if (!kp.hasPermission(permission)) {
            return false;
        }
        return kingdom.hasAttribute(land.getKingdom(), KingdomRelation.Attribute.BUILD);
    }

    @Override
    public boolean canCreateExplosion(@NotNull final Player player,
                                      @NotNull final Location location) {
        Land land = Land.getLand(location);
        if (land == null) {
            return true;
        }
        if (!land.isClaimed()) {
            return true;
        }

        Kingdom kingdom = land.getKingdom();
        return kingdom.isMember(player);
    }

    @Override
    public boolean canPlaceBlock(@NotNull final Player player,
                                 @NotNull final Block block) {
        return canBreakBlock(player, block);
    }

    @Override
    public boolean canInjure(@NotNull final Player player,
                             @NotNull final LivingEntity victim) {
        if (victim instanceof Player) {
            return PvPManager.canFight(player, (Player) victim);
        } else {
            Land land = Land.getLand(victim.getLocation());
            if (land == null) {
                return true;
            }
            if (!land.isClaimed()) {
                return true;
            }

            Kingdom kingdom = land.getKingdom();
            return kingdom.isMember(player);
        }
    }

    @Override
    public String getPluginName() {
        return "Kingdoms";
    }
}
