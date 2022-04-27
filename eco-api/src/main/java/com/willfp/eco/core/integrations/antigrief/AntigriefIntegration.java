package com.willfp.eco.core.integrations.antigrief;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper class for antigrief integrations.
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
     *
     * @param player   The player.
     * @param location The location.
     * @return If player can pick up item.
     */
    default boolean canPickupItem(@NotNull Player player, @NotNull Location location) {
        return true;
    }
}
