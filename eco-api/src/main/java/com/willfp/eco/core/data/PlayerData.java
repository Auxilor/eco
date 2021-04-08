package com.willfp.eco.core.data;

import com.willfp.eco.core.config.BaseConfig;
import com.willfp.eco.core.config.Config;
import com.willfp.eco.internal.config.ConfigSection;
import com.willfp.eco.internal.config.ConfigWrapper;
import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unchecked")
@UtilityClass
public class PlayerData {
    /**
     * Instance of eco data.yml.
     */
    private static BaseConfig dataYml = null;

    /**
     * All cached player data.
     */
    private static final Map<UUID, Config> PLAYER_DATA = new HashMap<>();

    /**
     * Write an integer to a player's data.
     *
     * @param player The player.
     * @param key    The key.
     * @param data   The data.
     */
    public void writeInt(@NotNull final OfflinePlayer player,
                         @NotNull final NamespacedKey key,
                         final int data) {
        Config config = getPlayerConfig(player);

        config.set("player-data." + player.getUniqueId() + "." + key.toString(), data);
    }

    /**
     * Write a string to a player's data.
     *
     * @param player The player.
     * @param key    The key.
     * @param data   The data.
     */
    public void writeString(@NotNull final OfflinePlayer player,
                            @NotNull final NamespacedKey key,
                            @NotNull final String data) {
        Config config = getPlayerConfig(player);

        config.set("player-data." + player.getUniqueId() + "." + key.toString(), data);
    }

    /**
     * Write a double to a player's data.
     *
     * @param player The player.
     * @param key    The key.
     * @param data   The data.
     */
    public void writeDouble(@NotNull final OfflinePlayer player,
                            @NotNull final NamespacedKey key,
                            final double data) {
        Config config = getPlayerConfig(player);

        config.set("player-data." + player.getUniqueId() + "." + key.toString(), data);
    }

    /**
     * Read an integer from a player's data.
     *
     * @param player The player.
     * @param key    The key.
     */
    public int readInt(@NotNull final OfflinePlayer player,
                       @NotNull final NamespacedKey key) {
        return getPlayerConfig(player).getInt("player-data." + player.getUniqueId() + "." + key.toString());
    }

    /**
     * Read a string from a player's data.
     *
     * @param player The player.
     * @param key    The key.
     */
    public String readString(@NotNull final OfflinePlayer player,
                             @NotNull final NamespacedKey key) {
        return getPlayerConfig(player).getString("player-data." + player.getUniqueId() + "." + key.toString());
    }

    /**
     * Read a double from a player's data.
     *
     * @param player The player.
     * @param key    The key.
     */
    public double readDouble(@NotNull final OfflinePlayer player,
                             @NotNull final NamespacedKey key) {
        return getPlayerConfig(player).getDouble("player-data." + player.getUniqueId() + "." + key.toString());
    }

    /**
     * Initialize the player data with an instance of data.yml.
     *
     * @param config data.yml.
     */
    @ApiStatus.Internal
    public void init(@NotNull final BaseConfig config) {
        dataYml = config;
    }

    /**
     * Save to data.yml.
     *
     * @param config Instance of data.yml.
     * @throws IOException Error during saving.
     */
    @ApiStatus.Internal
    public void save(@NotNull final BaseConfig config) throws IOException {
        PLAYER_DATA.forEach((uuid, section) -> config.set("player-data." + uuid.toString(), ((ConfigWrapper<ConfigurationSection>) section).getHandle()));
        config.save();
    }

    private Config getPlayerConfig(@NotNull final OfflinePlayer player) {
        Config config = PLAYER_DATA.get(player.getUniqueId());

        if (config == null) {
            config = dataYml.getSubsectionOrNull("player-data." + player.getUniqueId());
            if (config == null) {
                config = new ConfigSection(new YamlConfiguration());
            }
            PLAYER_DATA.put(player.getUniqueId(), config);
            return getPlayerConfig(player);
        }

        return config;
    }
}
