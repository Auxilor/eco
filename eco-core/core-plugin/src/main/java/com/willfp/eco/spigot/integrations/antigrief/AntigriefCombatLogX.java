package com.willfp.eco.spigot.integrations.antigrief;

import com.SirBlobman.combatlogx.expansion.newbie.helper.NewbieHelper;
import com.SirBlobman.combatlogx.expansion.newbie.helper.listener.ListenerPVP;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.willfp.eco.spigot.EcoPlugin;
import com.willfp.eco.util.integrations.antigrief.AntigriefWrapper;
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
        EcoPlugin.getInstance().getScheduler().runLater(() -> {
            Object expansionUncast = instance.getExpansionManager().getAllExpansions().stream().filter(ex -> ex.getName().toLowerCase().contains("newbie")).findFirst().orElse(null);
            if (expansionUncast != null) {
                if (expansionUncast.getClass().getName().equals(NewbieHelper.class.getName())) {
                    pvp = ((NewbieHelper) expansionUncast).getPVPListener();
                }
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
        return "Towny";
    }
}
