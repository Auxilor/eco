package com.willfp.eco.internal.config;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.config.ConfigSaveHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EcoConfigSaveHandler extends PluginDependent implements ConfigSaveHandler {
    private final List<LoadableConfig> configs = new ArrayList<>();

    /**
     * Pass an {@link EcoPlugin} in order to interface with it.
     *
     * @param plugin The plugin to manage.
     */
    public EcoConfigSaveHandler(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    @Override
    public void saveAllConfigs() {
        try {
            for (LoadableConfig config : configs) {
                config.save();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addConfig(@NotNull final LoadableConfig config) {
        configs.add(config);
    }
}
