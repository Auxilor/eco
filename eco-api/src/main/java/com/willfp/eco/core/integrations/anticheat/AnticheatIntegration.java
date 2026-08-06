package com.willfp.eco.core.integrations.anticheat;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper interface for anticheat integrations.
 * <p>
 * Implemented for anticheat plugins such as Spartan, Vulcan, Alice, and AAC, so that eco can
 * temporarily exempt players from anticheat checks while performing movement or combat that
 * would otherwise look like cheating.
 *
 * @see AnticheatManager
 */
public interface AnticheatIntegration extends Integration {
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
