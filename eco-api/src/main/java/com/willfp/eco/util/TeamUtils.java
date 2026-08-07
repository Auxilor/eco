package com.willfp.eco.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities / API methods for teams.
 */
public final class TeamUtils {
    /**
     * All chat color teams, keyed by the color they apply.
     */
    private static final BiMap<ChatColor, Team> CHAT_COLOR_TEAMS = HashBiMap.create();

    /**
     * The server scoreboard, on which the color teams are registered.
     */
    private static final Scoreboard SCOREBOARD = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();

    /**
     * Get team from {@link ChatColor}.
     * <p>
     * For {@link org.bukkit.potion.PotionEffectType#GLOWING}.
     * <p>
     * Teams are named {@code EC-} followed by the colour's name, and live on the main server
     * scoreboard. They are created on first use, then reused, and a team for every colour is
     * registered when this class loads. Only actual colours are supported, not formatting codes.
     *
     * @param color The color to find the team for.
     * @return The team, which has its colour set to the given colour.
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
