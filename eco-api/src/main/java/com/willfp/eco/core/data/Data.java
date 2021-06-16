package com.willfp.eco.core.data;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.Config;
import com.willfp.eco.core.config.JSONConfig;
import com.willfp.eco.core.config.JsonStaticBaseConfig;
import com.willfp.eco.internal.config.LoadableConfig;
import com.willfp.eco.internal.config.json.JSONConfigSection;
import lombok.experimental.UtilityClass;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class Data {
    /**
     * Instance of eco data.yml.
     */
    private static JSONConfig datafile = null;

    /**
     * All cached player data.
     */
    private static final Map<UUID, Map<EcoPlugin, JSONConfig>> PLAYER_DATA = new HashMap<>();

    /**
     * Initialize the player data with an instance of data.json.
     *
     * @param config data.json.
     */
    @ApiStatus.Internal
    public void init(@NotNull final JsonStaticBaseConfig config) {
        datafile = config;
    }

    /**
     * Save to data.yml.
     *
     * @param config Instance of data.yml.
     * @throws IOException Error during saving.
     */
    @ApiStatus.Internal
    public void save(@NotNull final Config config) throws IOException {
        for (Map.Entry<UUID, Map<EcoPlugin, JSONConfig>> entry : PLAYER_DATA.entrySet()) {
            entry.getValue().forEach((plugin, jsonConfig) -> {
                for (String key : jsonConfig.getKeys(false)) {
                    config.set("player-data." + plugin.getName().toLowerCase() + "." + entry.getKey().toString() + "." + key, jsonConfig);
                }
            });
        }
        ((LoadableConfig) config).save();
    }

    /**
     * Get the data for a player.
     *
     * @param player The player.
     * @param plugin The plugin.
     * @return The data.
     */
    public JSONConfig getData(@NotNull final OfflinePlayer player,
                              @NotNull final EcoPlugin plugin) {
        if (!PLAYER_DATA.containsKey(player.getUniqueId())) {
            PLAYER_DATA.put(player.getUniqueId(), new HashMap<>());
        }

        JSONConfig config = PLAYER_DATA.get(player.getUniqueId()).get(plugin);

        if (config == null) {
            config = (JSONConfig) datafile.getSubsectionOrNull("player-data." + plugin.getName().toLowerCase() + "." + player.getUniqueId());
            if (config == null) {
                config = new JSONConfigSection(new HashMap<>());
            }
            PLAYER_DATA.get(player.getUniqueId()).put(plugin, config);
            return getData(player, plugin);
        }

        return config;
    }
}
