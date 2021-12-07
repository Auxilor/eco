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
public interface AntigriefWrapper extends Integration {
    /**
     * Can player break block.
     *
     * @param player The player.
     * @param block  The block.
     * @return If player can break block.
     */
    boolean canBreakBlock(Player player, Block block);

    /**
     * Can player create explosion at location.
     *
     * @param player   The player.
     * @param location The location.
     * @return If player can create explosion.
     */
    boolean canCreateExplosion(Player player, Location location);

    /**
     * Can player place block.
     *
     * @param player The player.
     * @param block  The block.
     * @return If player can place block.
     */
    boolean canPlaceBlock(Player player, Block block);

    /**
     * Can player injure living entity.
     *
     * @param player The player.
     * @param victim The victim.
     * @return If player can injure.
     */
    boolean canInjure(Player player, LivingEntity victim);

    /**
     * Can player pick up item.
     *
     * @param player   The player.
     * @param location The location.
     * @return If player can pick up item.
     */
    boolean canPickupItem(@NotNull final Player player, @NotNull final Location location);

}
