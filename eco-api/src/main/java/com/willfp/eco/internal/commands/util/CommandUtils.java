package com.willfp.eco.internal.commands.util;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.CommandBase;
import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class CommandUtils {
    public boolean canExecute(@NotNull final CommandSender sender,
                              @NotNull final CommandBase command,
                              @NotNull final EcoPlugin plugin) {
        if (command.isPlayersOnly() && !(sender instanceof Player)) {
            sender.sendMessage(plugin.getLangYml().getMessage("not-player"));
            return false;
        }

        if (!sender.hasPermission(command.getPermission()) && sender instanceof Player) {
            sender.sendMessage(plugin.getLangYml().getNoPermission());
            return false;
        }

        return true;
    }
}
