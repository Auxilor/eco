package com.willfp.eco.spigot.eventlisteners;

import com.willfp.eco.core.events.PlayerJumpEvent;
import com.willfp.eco.core.integrations.mcmmo.McmmoManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class PlayerJumpListeners implements Listener {
    /**
     * For jump listeners.
     */
    private static final Set<UUID> PREVIOUS_PLAYERS_ON_GROUND = new HashSet<>();

    /**
     * For jump listeners.
     */
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");

    /**
     * Called when a player jumps.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onJump(@NotNull final PlayerMoveEvent event) {
        if (McmmoManager.isFake(event)) {
            return;
        }

        Player player = event.getPlayer();
        if (player.getVelocity().getY() > 0) {
            float jumpVelocity = 0.42f;
            if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                jumpVelocity += ((float) player.getPotionEffect(PotionEffectType.JUMP).getAmplifier() + 1) * 0.1F;
            }
            jumpVelocity = Float.parseFloat(FORMAT.format(jumpVelocity).replace(',', '.'));
            if (event.getPlayer().getLocation().getBlock().getType() != Material.LADDER
                    && PREVIOUS_PLAYERS_ON_GROUND.contains(player.getUniqueId())
                    && !player.isOnGround()
                    && Float.compare((float) player.getVelocity().getY(), jumpVelocity) == 0) {
                Bukkit.getPluginManager().callEvent(new PlayerJumpEvent(event));
            }
        }
        if (player.isOnGround()) {
            PREVIOUS_PLAYERS_ON_GROUND.add(player.getUniqueId());
        } else {
            PREVIOUS_PLAYERS_ON_GROUND.remove(player.getUniqueId());
        }
    }
}
