package com.willfp.eco.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Utilities / API methods for teams.
 */
public final class TeamUtils {
    /**
     * All chat color teams.
     */
    private static final BiMap<ChatColor, Team> CHAT_COLOR_TEAMS = HashBiMap.create();

    /**
     * The server scoreboard.
     */
    private static final Scoreboard SCOREBOARD = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();

    /**
     * Get team from {@link ChatColor}.
     * <p>
     * For {@link org.bukkit.potion.PotionEffectType#GLOWING}.
     *
     * @param color The color to find the team for.
     * @return The team.
     */
    @NotNull
    public static Team fromChatColor(@NotNull final ChatColor color) {
        if (CHAT_COLOR_TEAMS.containsKey(color)) {
            return CHAT_COLOR_TEAMS.get(color);
        }

        Team team;

        if (!SCOREBOARD.getTeams().stream().map(Team::getName).toList().contains("EC-" + color.name())) {
            team = SCOREBOARD.registerNewTeam("EC-" + color.name());
        } else {
            team = SCOREBOARD.getTeam("EC-" + color.name());
        }
        assert team != null;
        team.setColor(color);
        CHAT_COLOR_TEAMS.forcePut(color, team);

        return team;
    }

    static {
        for (ChatColor value : ChatColor.values()) {
            if (!value.isColor()) {
                continue;
            }
            fromChatColor(value);
        }
    }

    private TeamUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
