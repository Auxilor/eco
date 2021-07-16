package com.willfp.eco.internal.config.updating;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.config.interfaces.LoadableConfig;
import com.willfp.eco.core.config.updating.ConfigHandler;
import com.willfp.eco.core.config.updating.ConfigUpdater;
import com.willfp.eco.internal.config.updating.exceptions.InvalidUpdateMethodException;
import com.willfp.eco.internal.config.yaml.EcoUpdatableYamlConfig;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class EcoConfigHandler extends PluginDependent<EcoPlugin> implements ConfigHandler {
    private final List<LoadableConfig> configs = new ArrayList<>();

    private final Reflections reflections = new Reflections(this.getPlugin().getClass().getPackageName(), new MethodAnnotationsScanner());

    public EcoConfigHandler(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    @Override
    public void callUpdate() {
        for (Method method : reflections.getMethodsAnnotatedWith(ConfigUpdater.class)) {
            if (!Modifier.isStatic(method.getModifiers())) {
                throw new InvalidUpdateMethodException("Update method must be static.");
            }

            try {
                if (method.getParameterCount() == 0) {
                    method.invoke(null);
                } else if (method.getParameterCount() == 1) {
                    method.invoke(null, this.getPlugin());
                } else {
                    throw new InvalidUpdateMethodException("Update method must have 0 parameters or a plugin parameter.");
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                throw new InvalidUpdateMethodException("Update method generated an exception.");
            }
        }
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

    @Override
    public void updateConfigs() {
        for (LoadableConfig config : configs) {
            if (config instanceof EcoUpdatableYamlConfig updatableYamlConfig) {
                updatableYamlConfig.update();
            }
        }
    }
}
