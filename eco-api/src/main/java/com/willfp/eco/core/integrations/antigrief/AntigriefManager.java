package com.willfp.eco.core.integrations.antigrief;

import com.willfp.eco.core.integrations.IntegrationRegistry;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Class to handle antigrief integrations.
 */
public final class AntigriefManager {
    /**
     * Registered antigriefs.
     */
    private static final IntegrationRegistry<AntigriefIntegration> REGISTRY = new IntegrationRegistry<>();

    /**
     * Register a new AntiGrief/Land Management integration.
     *
     * @param antigrief The integration to register.
     */
    public static void register(@NotNull final AntigriefIntegration antigrief) {
        REGISTRY.register(antigrief);
    }

    /**
     * Unregister an AntiGrief/Land Management integration.
     *
     * @param antigrief The integration to unregister.
     */
    public static void unregister(@NotNull final AntigriefIntegration antigrief) {
        REGISTRY.remove(antigrief);
    }

    /**
     * Can player pickup item.
     *
     * @param player   The player.
     * @param location The location.
     * @return If player can pick up item.
     */
    public static boolean canPickupItem(@NotNull final Player player,
                                        @NotNull final Location location) {
        return REGISTRY.allSafely(integration -> integration.canPickupItem(player, location));
    }

    /**
     * Can player break block.
     *
     * @param player The player.
     * @param block  The block.
     * @return If player can break block.
     */
    public static boolean canBreakBlock(@NotNull final Player player,
                                        @NotNull final Block block) {
        return REGISTRY.allSafely(integration -> integration.canBreakBlock(player, block));
    }

    /**
     * Can player create explosion at location.
     *
     * @param player   The player.
     * @param location The location.
     * @return If player can create explosion.
     */
    public static boolean canCreateExplosion(@NotNull final Player player,
                                             @NotNull final Location location) {
        return REGISTRY.allSafely(integration -> integration.canCreateExplosion(player, location));
    }

    /**
     * Can player place block.
     *
     * @param player The player.
     * @param block  The block.
     * @return If player can place block.
     */
    public static boolean canPlaceBlock(@NotNull final Player player,
                                        @NotNull final Block block) {
        return REGISTRY.allSafely(integration -> integration.canPlaceBlock(player, block));
    }

    /**
     * Can player injure living entity.
     *
     * @param player The player.
     * @param victim The victim.
     * @return If player can injure.
     */
    public static boolean canInjure(@NotNull final Player player,
                                    @NotNull final LivingEntity victim) {
        return REGISTRY.allSafely(integration -> integration.canInjure(player, victim));
    }

    private AntigriefManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
