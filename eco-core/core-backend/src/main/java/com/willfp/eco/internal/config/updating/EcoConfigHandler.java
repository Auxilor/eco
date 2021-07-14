package com.willfp.eco.internal.config.updating;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.config.updating.ConfigHandler;
import com.willfp.eco.core.config.updating.ConfigUpdater;
import com.willfp.eco.core.config.LoadableConfig;
import com.willfp.eco.internal.config.updating.exceptions.InvalidUpdatableClassException;
import com.willfp.eco.internal.config.updating.exceptions.InvalidUpdateMethodException;
import com.willfp.eco.internal.config.yaml.UpdatableYamlConfig;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EcoConfigHandler extends PluginDependent<EcoPlugin> implements ConfigHandler {
    /**
     * A set of all configs that can be saved.
     */
    private final List<LoadableConfig> configs = new ArrayList<>();

    /**
     * A set of all classes that can be updated.
     */
    private final Set<Class<?>> updatableClasses = new HashSet<>();

    /**
     * Creates a new config handler and links it to an {@link EcoPlugin}.
     *
     * @param plugin The plugin to manage.
     */
    public EcoConfigHandler(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    @Override
    public void callUpdate() {
        updatableClasses.forEach(clazz -> Arrays.stream(clazz.getDeclaredMethods()).forEach(method -> {
            if (method.isAnnotationPresent(ConfigUpdater.class)) {
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
        }));
    }

    @Override
    public void registerUpdatableClass(@NotNull final Class<?> updatableClass) {
        boolean isValid = false;
        for (Method method : updatableClass.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers()) && (method.getParameterCount() == 0 || method.getParameterCount() == 1) && method.isAnnotationPresent(ConfigUpdater.class)) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            throw new InvalidUpdatableClassException("Registered updatable class " + updatableClass + " must have an annotated static method with no modifiers.");
        }

        updatableClasses.add(updatableClass);
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
            if (config instanceof UpdatableYamlConfig updatableYamlConfig) {
                updatableYamlConfig.update();
            }
        }
    }
}
