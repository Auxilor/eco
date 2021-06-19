package com.willfp.eco.internal.config.yaml;

import com.willfp.eco.core.config.Config;
import com.willfp.eco.util.StringUtils;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"unchecked", "unused"})
public abstract class YamlConfigWrapper<T extends ConfigurationSection> implements Config {
    /**
     * The linked {@link ConfigurationSection} where values are physically stored.
     */
    @Getter
    private T handle = null;

    /**
     * Cached values for faster reading.
     */
    private final Map<String, Object> cache = new HashMap<>();

    /**
     * Abstract config.
     */
    protected YamlConfigWrapper() {

    }

    protected Config init(@NotNull final T config) {
        this.handle = config;
        return this;
    }

    @Override
    public String toPlaintext() {
        YamlConfiguration temp = new YamlConfiguration();
        for (String key : handle.getKeys(true)) {
            temp.set(key, handle.get(key));
        }
        return temp.saveToString();
    }

    @Override
    public final void clearCache() {
        cache.clear();
    }

    @Override
    public boolean has(@NotNull final String path) {
        return handle.contains(path);
    }

    @NotNull
    @Override
    public List<String> getKeys(final boolean deep) {
        return new ArrayList<>(handle.getKeys(deep));
    }

    @Override
    @Nullable
    public Object get(@NotNull final String path) {
        return handle.get(path);
    }

    @Override
    public void set(@NotNull final String path,
                    @Nullable final Object object) {
        cache.remove(path);
        handle.set(path, object);
    }

    @Override
    @NotNull
    public Config getSubsection(@NotNull final String path) {
        Config subsection = getSubsectionOrNull(path);
        Validate.notNull(subsection);
        return subsection;
    }

    @Override
    @Nullable
    public Config getSubsectionOrNull(@NotNull final String path) {
        if (cache.containsKey(path)) {
            return (Config) cache.get(path);
        } else {
            ConfigurationSection raw = handle.getConfigurationSection(path);
            if (raw == null) {
                cache.put(path, null);
            } else {
                cache.put(path, new ConfigSection(raw));
            }
            return getSubsectionOrNull(path);
        }
    }

    @Override
    public int getInt(@NotNull final String path) {
        if (cache.containsKey(path)) {
            return (int) cache.get(path);
        } else {
            cache.put(path, handle.getInt(path, 0));
            return getInt(path);
        }
    }

    @Override
    @Nullable
    public Integer getIntOrNull(@NotNull final String path) {
        if (has(path)) {
            return getInt(path);
        } else {
            return null;
        }
    }

    @Override
    public int getInt(@NotNull final String path,
                      final int def) {
        if (cache.containsKey(path)) {
            return (int) cache.get(path);
        } else {
            cache.put(path, handle.getInt(path, def));
            return getInt(path);
        }
    }

    @Override
    @NotNull
    public List<Integer> getInts(@NotNull final String path) {
        if (cache.containsKey(path)) {
            return (List<Integer>) cache.get(path);
        } else {
            cache.put(path, has(path) ? new ArrayList<>(handle.getIntegerList(path)) : new ArrayList<>());
            return getInts(path);
        }
    }

    @Override
    @Nullable
    public List<Integer> getIntsOrNull(@NotNull final String path) {
        if (has(path)) {
            return getInts(path);
        } else {
            return null;
        }
    }

    @Override
    public boolean getBool(@NotNull final String path) {
        if (cache.containsKey(path)) {
            return (boolean) cache.get(path);
        } else {
            cache.put(path, handle.getBoolean(path));
            return getBool(path);
        }
    }

    @Override
    @Nullable
    public Boolean getBoolOrNull(@NotNull final String path) {
        if (has(path)) {
            return getBool(path);
        } else {
            return null;
        }
    }

    @Override
    @NotNull
    public List<Boolean> getBools(@NotNull final String path) {
        if (cache.containsKey(path)) {
            return (List<Boolean>) cache.get(path);
        } else {
            cache.put(path, has(path) ? new ArrayList<>(handle.getBooleanList(path)) : new ArrayList<>());
            return getBools(path);
        }
    }

    @Override
    @Nullable
    public List<Boolean> getBoolsOrNull(@NotNull final String path) {
        if (has(path)) {
            return getBools(path);
        } else {
            return null;
        }
    }

    @Override
    @NotNull
    public String getString(@NotNull final String path) {
        if (cache.containsKey(path)) {
            return (String) cache.get(path);
        } else {
            cache.put(path, StringUtils.translate(Objects.requireNonNull(handle.getString(path, ""))));
            return getString(path);
        }
    }

    @Override
    @Nullable
    public String getStringOrNull(@NotNull final String path) {
        if (has(path)) {
            return getString(path);
        } else {
            return null;
        }
    }

    @Override
    @NotNull
    public List<String> getStrings(@NotNull final String path) {
        if (cache.containsKey(path)) {
            return StringUtils.translateList((List<String>) cache.get(path));
        } else {
            cache.put(path, has(path) ? new ArrayList<>(handle.getStringList(path)) : new ArrayList<>());
            return getStrings(path);
        }
    }

    @Override
    @Nullable
    public List<String> getStringsOrNull(@NotNull final String path) {
        if (has(path)) {
            return getStrings(path);
        } else {
            return null;
        }
    }

    @Override
    public double getDouble(@NotNull final String path) {
        if (cache.containsKey(path)) {
            return (double) cache.get(path);
        } else {
            cache.put(path, handle.getDouble(path));
            return getDouble(path);
        }
    }

    @Override
    @Nullable
    public Double getDoubleOrNull(@NotNull final String path) {
        if (has(path)) {
            return getDouble(path);
        } else {
            return null;
        }
    }

    @Override
    @NotNull
    public List<Double> getDoubles(@NotNull final String path) {
        if (cache.containsKey(path)) {
            return (List<Double>) cache.get(path);
        } else {
            cache.put(path, has(path) ? new ArrayList<>(handle.getDoubleList(path)) : new ArrayList<>());
            return getDoubles(path);
        }
    }

    @Override
    @Nullable
    public List<Double> getDoublesOrNull(@NotNull final String path) {
        if (has(path)) {
            return getDoubles(path);
        } else {
            return null;
        }
    }
}
