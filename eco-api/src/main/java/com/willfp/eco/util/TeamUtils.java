package com.willfp.eco.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.willfp.eco.core.Prerequisite;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class TeamUtils {
    /**
     * Ore ChatColors.
     */
    private static final Map<Material, ChatColor> MATERIAL_COLORS = new HashMap<>();

    /**
     * All chat color teams.
     */
    private static final BiMap<ChatColor, Team> CHAT_COLOR_TEAMS = HashBiMap.create();

    /**
     * The server scoreboard.
     */
    private static final Scoreboard SCOREBOARD = Bukkit.getScoreboardManager().getMainScoreboard();

    /**
     * Get team from {@link ChatColor}.
     * <p>
     * For {@link org.bukkit.potion.PotionEffectType#GLOWING}.
     *
     * @param color The color to find the team for.
     * @return The team.
     */
    public Team fromChatColor(@NotNull final ChatColor color) {
        if (CHAT_COLOR_TEAMS.containsKey(color)) {
            return CHAT_COLOR_TEAMS.get(color);
        }

        Team team;

        if (!SCOREBOARD.getTeams().stream().map(Team::getName).collect(Collectors.toList()).contains("EC-" + color.name())) {
            team = SCOREBOARD.registerNewTeam("EC-" + color.name());
        } else {
            team = SCOREBOARD.getTeam("EC-" + color.name());
        }
        assert team != null;
        team.setColor(color);
        CHAT_COLOR_TEAMS.forcePut(color, team);

        return team;
    }

    /**
     * Get team from material.
     * <p>
     * For {@link org.bukkit.potion.PotionEffectType#GLOWING}.
     *
     * @param material The material to find the team from.
     * @return The team.
     */
    public Team getMaterialColorTeam(@NotNull final Material material) {
        return fromChatColor(MATERIAL_COLORS.getOrDefault(material, ChatColor.WHITE));
    }

    static {
        for (ChatColor value : ChatColor.values()) {
            fromChatColor(value);
        }

        MATERIAL_COLORS.put(Material.COAL_ORE, ChatColor.BLACK);
        MATERIAL_COLORS.put(Material.IRON_ORE, ChatColor.GRAY);
        MATERIAL_COLORS.put(Material.GOLD_ORE, ChatColor.YELLOW);
        MATERIAL_COLORS.put(Material.LAPIS_ORE, ChatColor.BLUE);
        MATERIAL_COLORS.put(Material.REDSTONE_ORE, ChatColor.RED);
        MATERIAL_COLORS.put(Material.DIAMOND_ORE, ChatColor.AQUA);
        MATERIAL_COLORS.put(Material.EMERALD_ORE, ChatColor.GREEN);
        MATERIAL_COLORS.put(Material.ANCIENT_DEBRIS, ChatColor.DARK_RED);

        if (Prerequisite.HAS_1_17.isMet()) {
            MATERIAL_COLORS.put(Material.COPPER_ORE, ChatColor.GOLD);
            MATERIAL_COLORS.put(Material.DEEPSLATE_COPPER_ORE, ChatColor.GOLD);
            MATERIAL_COLORS.put(Material.DEEPSLATE_COAL_ORE, ChatColor.BLACK);
            MATERIAL_COLORS.put(Material.DEEPSLATE_IRON_ORE, ChatColor.GRAY);
            MATERIAL_COLORS.put(Material.DEEPSLATE_GOLD_ORE, ChatColor.YELLOW);
            MATERIAL_COLORS.put(Material.DEEPSLATE_LAPIS_ORE, ChatColor.BLUE);
            MATERIAL_COLORS.put(Material.DEEPSLATE_REDSTONE_ORE, ChatColor.RED);
            MATERIAL_COLORS.put(Material.DEEPSLATE_DIAMOND_ORE, ChatColor.AQUA);
            MATERIAL_COLORS.put(Material.DEEPSLATE_EMERALD_ORE, ChatColor.GREEN);
        }
    }
}
