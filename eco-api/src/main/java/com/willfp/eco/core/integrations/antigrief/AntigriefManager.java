package com.willfp.eco.core.integrations.antigrief;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to handle antigrief integrations.
 */
public final class AntigriefManager {
    /**
     * Registered antigriefs.
     */
    private static final Set<AntigriefWrapper> REGISTERED = new HashSet<>();

    /**
     * Register a new AntiGrief/Land Management integration.
     *
     * @param antigrief The integration to register.
     */
    public static void register(@NotNull final AntigriefWrapper antigrief) {
        REGISTERED.add(antigrief);
    }

    /**
     * Unregister an AntiGrief/Land Management integration.
     *
     * @param antigrief The integration to unregister.
     */
    public static void unregister(@NotNull final AntigriefWrapper antigrief) {
        REGISTERED.removeIf(it -> it.getPluginName().equalsIgnoreCase(antigrief.getPluginName()));
        REGISTERED.remove(antigrief);
    }

    /**
     * Can player pickup item.
     *
     * @param player   The player.
     * @param location The location.
     * @return If player can pick up item.
     */
    public static boolean canPickupItem(@NotNull final Player player, @NotNull final Location location) {
        return REGISTERED.stream().allMatch(antigriefWrapper -> antigriefWrapper.canPickupItem(player, location));
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
        return REGISTERED.stream().allMatch(antigriefWrapper -> antigriefWrapper.canBreakBlock(player, block));
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
        return REGISTERED.stream().allMatch(antigriefWrapper -> antigriefWrapper.canCreateExplosion(player, location));
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
        return REGISTERED.stream().allMatch(antigriefWrapper -> antigriefWrapper.canPlaceBlock(player, block));
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
        return REGISTERED.stream().allMatch(antigriefWrapper -> antigriefWrapper.canInjure(player, victim));
    }

    private AntigriefManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
