package com.willfp.eco.spigot.integrations.antigrief;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;
import com.willfp.eco.core.integrations.antigrief.AntigriefWrapper;
import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.PVPManager;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AntigriefCombatLogXV11 implements AntigriefWrapper {
    /**
     * Instance of CombatLogX.
     */
    private final ICombatLogX instance;

    /**
     * Create new CombatLogX antigrief.
     */
    public AntigriefCombatLogXV11() {
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
        Optional<Expansion> optionalExpansion = expansionManager.getExpansion("NewbieHelper");
        if(optionalExpansion.isPresent()) {
            Expansion expansion = optionalExpansion.get();
            NewbieHelperExpansion newbieHelperExpansion = (NewbieHelperExpansion) expansion;

            ProtectionManager protectionManager = newbieHelperExpansion.getProtectionManager();
            PVPManager pvpManager = newbieHelperExpansion.getPVPManager();

            Player victimPlayer = (Player) victim;
            boolean victimProtected = protectionManager.isProtected(victimPlayer);
            boolean victimDisabledPvP = pvpManager.isDisabled(victimPlayer);
            boolean playerDisabledPvp = pvpManager.isDisabled(player);
            return (!victimProtected && !victimDisabledPvP && !playerDisabledPvp);
        }

        return true;
    }

    @Override
    public String getPluginName() {
        return "CombatLogX";
    }
}
