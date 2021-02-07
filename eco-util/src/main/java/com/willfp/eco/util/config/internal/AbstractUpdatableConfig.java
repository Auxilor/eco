package com.willfp.eco.util.config.internal;

import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractUpdatableConfig extends AbstractConfig {
    /**
     * Whether keys not in the base config should be removed on update.
     */
    private final boolean removeUnused;

    /**
     * List of blacklisted update keys.
     */
    private final List<String> updateBlacklist;

    /**
     * Updatable config.
     *
     * @param configName       The name of the config
     * @param plugin           The plugin.
     * @param subDirectoryPath The subdirectory path.
     * @param source           The class that owns the resource.
     * @param removeUnused     Whether keys not present in the default config should be removed on update.
     * @param updateBlacklist  Substring of keys to not add/remove keys for.
     */
    protected AbstractUpdatableConfig(@NotNull final String configName,
                                      @NotNull final AbstractEcoPlugin plugin,
                                      @NotNull final String subDirectoryPath,
                                      @NotNull final Class<?> source,
                                      final boolean removeUnused,
                                      @NotNull final String... updateBlacklist) {
        super(configName, plugin, subDirectoryPath, source);
        this.removeUnused = removeUnused;
        this.updateBlacklist = new ArrayList<>(Arrays.asList(updateBlacklist));
        this.updateBlacklist.removeIf(String::isEmpty);

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

            YamlConfiguration newConfig = this.getConfigInJar();

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
