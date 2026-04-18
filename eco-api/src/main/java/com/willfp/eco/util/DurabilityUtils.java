package com.willfp.eco.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities / API methods for item durability.
 */
// Have to suppress casts to ItemMeta because the methods don't exist for some older versions that eco supports.
@SuppressWarnings("RedundantCast")
public final class DurabilityUtils {
    /**
     * Damage an item in a player's inventory.
     *
     * @param player The player.
     * @param item   The item to damage.
     * @param damage The amount of damage to deal.
     */
    public static void damageItem(@NotNull final Player player,
                                  @NotNull final ItemStack item,
                                  final int damage) {
        if (item.getItemMeta() == null) {
            return;
        }

        if (item.getItemMeta().isUnbreakable()) {
            return;
        }

        if (!(item.getItemMeta() instanceof Damageable meta)) {
            return;
        }

        // Special edge case
        if (item.getType() == Material.CARVED_PUMPKIN || item.getType() == Material.PLAYER_HEAD) {
            return;
        }

        PlayerItemDamageEvent event3 = new PlayerItemDamageEvent(player, item, damage);
        Bukkit.getPluginManager().callEvent(event3);

        if (!event3.isCancelled()) {
            int damage2 = event3.getDamage();
            meta.setDamage(meta.getDamage() + damage2);

            if (meta.getDamage() >= item.getType().getMaxDurability()) {
                meta.setDamage(item.getType().getMaxDurability());
                item.setItemMeta((ItemMeta) meta);
                PlayerItemBreakEvent event = new PlayerItemBreakEvent(player, item);
                Bukkit.getPluginManager().callEvent(event);
                item.setType(Material.AIR);
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1, 1);
            } else {
                item.setItemMeta((ItemMeta) meta);
            }
        }
    }

    /**
     * Damage an item.
     *
     * @param item   The item to damage.
     * @param damage The amount of damage to deal.
     */
    public static void damageItem(@NotNull final ItemStack item,
                                  final int damage) {
        if (item.getItemMeta() == null) {
            return;
        }

        if (item.getItemMeta().isUnbreakable()) {
            return;
        }

        if (!(item.getItemMeta() instanceof Damageable)) {
            return;
        }

        // Special edge case
        if (item.getType() == Material.CARVED_PUMPKIN || item.getType() == Material.PLAYER_HEAD) {
            return;
        }

        // Suppression because when I fix it, it causes weird compile bugs.
        @SuppressWarnings("PatternVariableCanBeUsed") Damageable meta = (Damageable) item.getItemMeta();
        meta.setDamage(meta.getDamage() + damage);

        if (meta.getDamage() >= item.getType().getMaxDurability()) {
            meta.setDamage(item.getType().getMaxDurability());
            item.setItemMeta((ItemMeta) meta);
            item.setType(Material.AIR);
        } else {
            item.setItemMeta((ItemMeta) meta);
        }
    }

    /**
     * Damage an item in a player's inventory without breaking it.
     *
     * @param item   The item to damage.
     * @param damage The amount of damage to deal.
     * @param player The player.
     */
    public static void damageItemNoBreak(@NotNull final ItemStack item,
                                         final int damage,
                                         @NotNull final Player player) {
        if (item.getItemMeta() == null) {
            return;
        }

        if (item.getItemMeta().isUnbreakable()) {
            return;
        }

        if (!(item.getItemMeta() instanceof Damageable meta)) {
            return;
        }

        PlayerItemDamageEvent event3 = new PlayerItemDamageEvent(player, item, damage);
        Bukkit.getPluginManager().callEvent(event3);

        if (!event3.isCancelled()) {
            int damage2 = event3.getDamage();
            meta.setDamage(meta.getDamage() + damage2);

            if (meta.getDamage() >= item.getType().getMaxDurability()) {
                meta.setDamage(item.getType().getMaxDurability() - 1);
            }
            item.setItemMeta((ItemMeta) meta);
        }
    }

    /**
     * Repair an item in a player's inventory.
     *
     * @param item   The item to damage.
     * @param repair The amount of damage to heal.
     */
    public static void repairItem(@NotNull final ItemStack item,
                                  final int repair) {
        if (item.getItemMeta() == null) {
            return;
        }

        if (item.getItemMeta().isUnbreakable()) {
            return;
        }

        if (item.getItemMeta() instanceof Damageable meta) {
            meta.setDamage(Math.max(0, meta.getDamage() - repair));

            item.setItemMeta((ItemMeta) meta);
        }
    }

    private DurabilityUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
