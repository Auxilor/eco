package com.willfp.eco.util;

import com.willfp.eco.core.Eco;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.audience.Audience;
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
        return Eco.getHandler().getAdventure().player(player);
    }

    /**
     * Get the audience from a command sender.
     *
     * @param sender The command sender.
     * @return The audience.
     */
    @NotNull
    public Audience getAudience(@NotNull final CommandSender sender) {
        return Eco.getHandler().getAdventure().sender(sender);
    }
}
