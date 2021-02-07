package com.willfp.eco.util.config;

import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public abstract class BaseConfig extends StaticBaseConfig {
    /**
     * Whether keys not in the base config should be removed on update.
     */
    private final boolean removeUnused;

    /**
     * List of blacklisted update keys.
     */
    private final List<String> updateBlacklist;

    /**
     * Config implementation for configs present in the plugin's base directory (eg config.yml, lang.yml).
     * <p>
     * Automatically updates.
     *
     * @param configName      The name of the config
     * @param removeUnused    Whether keys not present in the default config should be removed on update.
     * @param plugin          The plugin.
     * @param updateBlacklist Substring of keys to not add/remove keys for.
     */
    protected BaseConfig(@NotNull final String configName,
                         final boolean removeUnused,
                         @NotNull final AbstractEcoPlugin plugin,
                         @Nullable final String... updateBlacklist) {
        super(configName, plugin);
        this.removeUnused = removeUnused;
        this.updateBlacklist = Arrays.asList(updateBlacklist);

        update();
    }

    /**
     * Update the config.
     * <p>
     * Writes missing values, however removes comments due to how configs are stored internally in bukkit.
     */
    public void update() {
        try {
            config.load(this.getConfigFile());

            InputStream newIn = this.getPlugin().getResource(this.getName());
            if (newIn == null) {
                this.getPlugin().getLog().error(this.getName() + " is null?");
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(newIn, StandardCharsets.UTF_8));
            YamlConfiguration newConfig = new YamlConfiguration();
            newConfig.load(reader);

            if (newConfig.getKeys(true).equals(config.getKeys(true))) {
                return;
            }

            newConfig.getKeys(true).forEach((s -> {
                if (!config.getKeys(true).contains(s)) {
                    if (updateBlacklist.stream().noneMatch(s::contains)) {
                        config.set(s, newConfig.get(s));
                    }
                }
            }));

            if (this.removeUnused) {
                config.getKeys(true).forEach((s -> {
                    if (!newConfig.getKeys(true).contains(s)) {
                        if (updateBlacklist.stream().noneMatch(s::contains)) {
                            config.set(s, null);
                        }
                    }
                }));
            }

            config.save(this.getConfigFile());
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
