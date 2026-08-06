package com.willfp.eco.core.integrations.antigrief;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper interface for antigrief, land claim, and combat protection integrations.
 * <p>
 * Implemented for plugins such as WorldGuard, GriefPrevention, Towny, Lands, Kingdoms,
 * BentoBox, SuperiorSkyblock2, and CombatLogX, so that eco can check whether a player is
 * allowed to perform an action at a given location before doing it on their behalf.
 * <p>
 * All methods are permissive by convention: return {@code true} if the action is allowed.
 *
 * @see AntigriefManager
 */
public interface AntigriefIntegration extends Integration {
    /**
     * Can player break block.
     *
     * @param player The player.
     * @param block  The block.
     * @return If player can break block.
     */
    boolean canBreakBlock(@NotNull Player player, @NotNull Block block);

    /**
     * Can player create explosion at location.
     *
     * @param player   The player.
     * @param location The location.
     * @return If player can create explosion.
     */
    boolean canCreateExplosion(@NotNull Player player, @NotNull Location location);

    /**
     * Can player place block.
     *
     * @param player The player.
     * @param block  The block.
     * @return If player can place block.
     */
    boolean canPlaceBlock(@NotNull Player player, @NotNull Block block);

    /**
     * Can player injure living entity.
     *
     * @param player The player.
     * @param victim The victim.
     * @return If player can injure.
     */
    boolean canInjure(@NotNull Player player, @NotNull LivingEntity victim);

    /**
     * Can player pick up item.
     * <p>
     * Defaults to true; override only if the plugin protects item pickup.
     *
     * @param player   The player.
     * @param location The location.
     * @return If player can pick up item.
     */
    default boolean canPickupItem(@NotNull Player player, @NotNull Location location) {
        return true;
    }
}
