package com.willfp.eco.spigot.integrations.antigrief;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
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
        return true;
    }

    @Override
    public String getPluginName() {
        return "Towny";
    }
}
