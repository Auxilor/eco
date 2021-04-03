package com.willfp.eco.spigot.integrations.antigrief;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.expansion.newbie.helper.NewbieHelper;
import com.SirBlobman.combatlogx.expansion.newbie.helper.listener.ListenerPVP;
import com.willfp.eco.spigot.EcoSpigotPlugin;
import com.willfp.eco.core.integrations.antigrief.AntigriefWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AntigriefCombatLogX implements AntigriefWrapper {
    /**
     * Instance of CombatLogX.
     */
    private final ICombatLogX instance = (ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");

    /**
     * PVPManager for CombatLogX NewbieHelper.
     */
    private ListenerPVP pvp = null;

    /**
     * Create new CombatLogX antigrief.
     */
    public AntigriefCombatLogX() {
        assert instance != null;
        EcoSpigotPlugin.getInstance().getScheduler().runLater(() -> {
            Expansion expansionUncast = instance.getExpansionManager().getExpansionByName("NewbieHelper").orElse(null);
            if (expansionUncast instanceof NewbieHelper) {
                pvp = ((NewbieHelper) expansionUncast).getPVPListener();
            }
        }, 3);
    }

    @Override
    public boolean canBreakBlock(@NotNull final Player player,
                                 @NotNull final Block block) {
        return true;
    }

    @Override
    public boolean canCreateExplosion(@NotNull final Player player,
                                      @NotNull final Location location) {
        return true;
    }

    @Override
    public boolean canPlaceBlock(@NotNull final Player player,
                                 @NotNull final Block block) {
        return true;
    }

    @Override
    public boolean canInjure(@NotNull final Player player,
                             @NotNull final LivingEntity victim) {
        if (!(victim instanceof Player)) {
            return true;
        }

        if (pvp == null) {
            return true;
        }

        return (pvp.isPVPEnabled(player) && pvp.isPVPEnabled((Player) victim));
    }

    @Override
    public String getPluginName() {
        return "CombatLogX";
    }
}
