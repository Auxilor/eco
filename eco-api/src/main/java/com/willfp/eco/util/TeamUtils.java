package com.willfp.eco.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.willfp.eco.core.FoliaSupport;
import com.willfp.eco.core.Prerequisite;
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
    private static final BiMap<ChatColor, Team> CHAT_COLOR_TEAMS = Maps.synchronizedBiMap(HashBiMap.create());

    /**
     * Holder for the server scoreboard, on which the color teams are registered.
     * <p>
     * Resolved lazily, on first call to {@link #scoreboard()}, rather than eagerly here or on
     * {@link TeamUtils}, so that merely loading {@link TeamUtils} cannot fail on a server with
     * no scoreboard manager. The JVM initialises a nested class at most once, on first active
     * use, and blocks every other thread attempting to use it until that initialisation
     * completes (JLS 12.4.2) — so this is a race-free lazy singleton with no synchronisation
     * cost on the read path, unlike a hand-rolled null-check-then-assign.
     * <p>
     * This class is loaded only from {@link #scoreboard()}, which is only reached past the
     * {@link FoliaSupport#requireSupported} gate in {@link #fromChatColor(ChatColor)}. On
     * Folia that gate throws before {@link #scoreboard()} is ever called, so this holder is
     * never loaded there, and cannot throw during any class's {@code <clinit>}.
     */
    private static final class ScoreboardHolder {
        /**
         * The server scoreboard.
         */
        private static final Scoreboard SCOREBOARD =
                Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
    }

    /**
     * Get the server scoreboard, resolving it on first use.
     *
     * @return The server scoreboard.
     */
    @NotNull
    private static Scoreboard scoreboard() {
        return ScoreboardHolder.SCOREBOARD;
    }

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
        FoliaSupport.requireSupported("Chat colour teams");

        if (CHAT_COLOR_TEAMS.containsKey(color)) {
            return CHAT_COLOR_TEAMS.get(color);
        }

        Team team;

        if (!scoreboard().getTeams().stream().map(Team::getName).toList().contains("EC-" + color.name())) {
            team = scoreboard().registerNewTeam("EC-" + color.name());
        } else {
            team = scoreboard().getTeam("EC-" + color.name());
        }
        assert team != null;
        team.setColor(color);
        CHAT_COLOR_TEAMS.forcePut(color, team);

        return team;
    }

    static {
        // Chat colour teams are not supported on Folia (see fromChatColor's requireSupported
        // gate below), so this pre-population must not run there: calling fromChatColor per
        // colour would throw from inside <clinit>, which the JVM wraps as an
        // ExceptionInInitializerError, permanently poisoning this class for every future
        // caller with NoClassDefFoundError instead of the intended clean, single-warning,
        // per-call UnsupportedOperationException. Gating here, directly on the same
        // Prerequisite that FoliaSupport itself gates on, keeps this static block a no-op on
        // Folia while leaving Paper and Spigot (where HAS_FOLIA.isMet() is false) running the
        // exact same loop, over the exact same colours, in the exact same order, as before.
        if (!Prerequisite.HAS_FOLIA.isMet()) {
            for (ChatColor value : ChatColor.values()) {
                if (!value.isColor()) {
                    continue;
                }
                fromChatColor(value);
            }
        }
    }

    private TeamUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
