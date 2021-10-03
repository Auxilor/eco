package com.willfp.eco.util;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.Prerequisite;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities / API methods for players.
 */
@UtilityClass
public class PlayerUtils {
    /**
     * Get the audience from a player.
     *
     * @param player The player.
     * @return The audience.
     */
    @NotNull
    public Audience getAudience(@NotNull final Player player) {
        BukkitAudiences adventure = Eco.getHandler().getAdventure();

        if (Prerequisite.HAS_PAPER.isMet()) {
            if (player instanceof Audience) {
                return (Audience) player;
            } else {
                return Audience.empty();
            }
        } else {
            if (adventure == null) {
                return Audience.empty();
            } else {
                return adventure.player(player);
            }
        }
    }

    /**
     * Get the audience from a command sender.
     *
     * @param sender The command sender.
     * @return The audience.
     */
    @NotNull
    public Audience getAudience(@NotNull final CommandSender sender) {
        BukkitAudiences adventure = Eco.getHandler().getAdventure();

        if (Prerequisite.HAS_PAPER.isMet()) {
            if (sender instanceof Audience) {
                return (Audience) sender;
            } else {
                return Audience.empty();
            }
        } else {
            if (adventure == null) {
                return Audience.empty();
            } else {
                return adventure.sender(sender);
            }
        }
    }
}
