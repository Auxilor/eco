package com.willfp.eco.spigot.integrations.anticheat;

import com.willfp.eco.core.integrations.anticheat.AnticheatWrapper;
import me.konsolas.aac.api.AACAPI;
import me.konsolas.aac.api.AACExemption;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AnticheatAAC implements AnticheatWrapper, Listener {
    /**
     * AAC exemption for eco.
     */
    private final AACExemption ecoExemption = new AACExemption("eco");

    /**
     * AAC api.
     */
    private final AACAPI api = Objects.requireNonNull(Bukkit.getServicesManager().load(AACAPI.class));

    @Override
    public String getPluginName() {
        return "AAC";
    }

    @Override
    public void exempt(@NotNull final Player player) {
        api.addExemption(player, ecoExemption);
    }

    @Override
    public void unexempt(@NotNull final Player player) {
        api.removeExemption(player, ecoExemption);
    }
}
