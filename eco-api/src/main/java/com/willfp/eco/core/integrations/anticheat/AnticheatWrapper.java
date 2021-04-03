package com.willfp.eco.core.integrations.anticheat;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface AnticheatWrapper extends Integration {
    /**
     * Exempt a player from checks.
     *
     * @param player The player to exempt.
     */
    void exempt(@NotNull Player player);

    /**
     * Unexempt a player from checks.
     *
     * @param player The player to unexempt.
     */
    void unexempt(@NotNull Player player);
}
