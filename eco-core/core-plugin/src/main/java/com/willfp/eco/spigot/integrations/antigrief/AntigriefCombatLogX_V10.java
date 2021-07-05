package com.willfp.eco.spigot.integrations.antigrief;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.expansion.newbie.helper.NewbieHelper;
import com.SirBlobman.combatlogx.expansion.newbie.helper.listener.ListenerPVP;
import com.willfp.eco.core.integrations.antigrief.AntigriefWrapper;
import org.jetbrains.annotations.NotNull;

public class AntigriefCombatLogX_V10 implements AntigriefWrapper {
    /**
     * Instance of CombatLogX.
     */
    private final ICombatLogX instance;

    /**
     * Create new CombatLogX antigrief.
     */
    public AntigriefCombatLogX_V10() {
        this.instance = (ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
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

        // Only run checks if the NewbieHelper expansion is installed on the server.
        ExpansionManager expansionManager = this.instance.getExpansionManager();
        Optional<Expansion> optionalExpansion = expansionManager.getExpansionByName("NewbieHelper");
        if(optionalExpansion.isPresent()) {
            Expansion expansion = optionalExpansion.get();
            NewbieHelper newbieHelper = (NewbieHelper) expansion;
            ListenerPVP pvpListener = newbieHelper.getPVPListener();
            return (pvpListener.isPVPEnabled(player) && pvpListener.isPVPEnabled((Player) victim));
        }

        return true;
    }

    @Override
    public String getPluginName() {
        return "CombatLogX";
    }
}
