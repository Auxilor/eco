package com.willfp.eco.spigot.integrations.anticheat;

import com.willfp.eco.core.integrations.anticheat.AnticheatWrapper;
import me.frep.vulcan.api.event.VulcanFlagEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AnticheatVulcan implements AnticheatWrapper, Listener {
    private final Set<UUID> exempt = new HashSet<>();

    @Override
    public String getPluginName() {
        return "Vulcan";
    }

    @Override
    public void exempt(@NotNull final Player player) {
        this.exempt.add(player.getUniqueId());
    }

    @Override
    public void unexempt(@NotNull final Player player) {
        this.exempt.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onViolate(@NotNull final VulcanFlagEvent event) {
        if (!exempt.contains(event.getPlayer().getUniqueId())) {
            return;
        }

        event.setCancelled(true);
    }
}
