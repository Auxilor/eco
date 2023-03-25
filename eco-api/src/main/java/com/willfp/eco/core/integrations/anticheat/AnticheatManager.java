package com.willfp.eco.core.integrations.anticheat;

import com.willfp.eco.core.Eco;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to handle anticheat integrations.
 */
public final class AnticheatManager {
    /**
     * A set of all registered anticheats.
     */
    private static final Set<AnticheatIntegration> ANTICHEATS = new HashSet<>();

    /**
     * Register a new anticheat.
     *
     * @param anticheat The anticheat to register.
     */
    public static void register(@NotNull final AnticheatIntegration anticheat) {
        if (anticheat instanceof Listener) {
            Eco.get().getEcoPlugin().getEventManager().registerListener((Listener) anticheat);
        }
        ANTICHEATS.removeIf(it -> it.getPluginName().equalsIgnoreCase(anticheat.getPluginName()));
        ANTICHEATS.add(anticheat);
    }

    /**
     * Exempt a player from triggering anticheats.
     *
     * @param player The player to exempt.
     */
    public static void exemptPlayer(@NotNull final Player player) {
        ANTICHEATS.forEach(anticheat -> anticheat.exempt(player));
    }

    /**
     * Unexempt a player from triggering anticheats.
     * This is ran a tick after it is called to ensure that there are no event timing conflicts.
     *
     * @param player The player to remove the exemption.
     */
    public static void unexemptPlayer(@NotNull final Player player) {
        ANTICHEATS.forEach(anticheat -> anticheat.unexempt(player));
    }

    private AnticheatManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
