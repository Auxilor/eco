package com.willfp.eco.core.command.util;

import com.willfp.eco.core.command.CommandBase;
import com.willfp.eco.internal.Internals;
import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class CommandUtils {
    /**
     * Check if the sender can execute a command.
     *
     * @param sender  The sender.
     * @param command The command.
     * @return If possible. Sends messages.
     */
    public boolean canExecute(@NotNull final CommandSender sender,
                              @NotNull final CommandBase command) {
        if (command.isPlayersOnly() && !(sender instanceof Player)) {
            sender.sendMessage(Internals.getInstance().getPlugin().getLangYml().getMessage("not-player"));
            return false;
        }

        if (!sender.hasPermission(command.getPermission()) && sender instanceof Player) {
            sender.sendMessage(Internals.getInstance().getPlugin().getLangYml().getNoPermission());
            return false;
        }

        return true;
    }
}
