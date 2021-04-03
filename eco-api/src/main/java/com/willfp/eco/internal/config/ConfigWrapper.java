package com.willfp.eco.internal.config;

import com.willfp.eco.util.SerializationUtils;
import com.willfp.eco.util.StringUtils;
import com.willfp.eco.util.config.Config;
import com.willfp.eco.util.serialization.EcoSerializable;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"unchecked", "unused", "DeprecatedIsStillUsed"})
public abstract class ConfigWrapper<T extends ConfigurationSection> implements Config {
    /**
     * The linked {@link MemorySection} where values are physically stored.
     */
    @Getter(AccessLevel.PROTECTED)
    private T config = null;

    /**
     * Cached values for faster reading.
     */
    private final Map<String, Object> cache = new HashMap<>();

    /**
     * Abstract config.
     */
    protected ConfigWrapper() {

    }

    protected Config init(@NotNull final T config) {
        this.config = config;
        return this;
    }

    @Override
    public final void clearCache() {
        cache.clear();
    }

    @Override
    public boolean has(@NotNull final String path) {
        return config.contains(path);
    }

    @NotNull
    @Override
    public List<String> getKeys(final boolean deep) {
        return new ArrayList<>(config.getKeys(deep));
    }

    @Override
    public void set(@NotNull final String path,
                    @NotNull final EcoSerializable<?> object) {
        Config serializedConfig = object.serialize();
        for (String key : serializedConfig.getKeys(true)) {
            Object raw = serializedConfig.getRaw(key);
            config.set(path + "." + key, raw);
            cache.put(path + "." + key, raw);
        }
    }

    @Override
    @Nullable
    public Object getRaw(@NotNull final String path) {
        return config.get(path);
    }

    @Override
    @NotNull
    public <T extends EcoSerializable<T>> T get(@NotNull final String path,
                                                @NotNull final Class<T> clazz) {
        T object = getOrNull(path, clazz);
        if (object == null) {
            throw new NullPointerException("Object cannot be null!");
        } else {
            return object;
        }
    }

    @Override
    @Nullable
    public <T extends EcoSerializable<T>> T getOrNull(@NotNull final String path,
                                                      @NotNull final Class<T> clazz) {
        if (cache.containsKey(path)) {
            return (T) cache.get(path);
        } else {
            Config section = getSubsectionOrNull(path);
            if (section == null) {
                return null;
            }
            cache.put(path, SerializationUtils.deserialize(section, clazz));
            return getOrNull(path, clazz);
        }
    }

    @Override
    @NotNull
    @Deprecated
    public ConfigurationSection getSection(@NotNull final String path) {
        ConfigurationSection section = getSectionOrNull(path);
        if (section == null) {
            throw new NullPointerException("Section cannot be null!");
        } else {
            return section;
        }
    }

    @Override
    @Nullable
    @Deprecated
    public ConfigurationSection getSectionOrNull(@NotNull final String path) {
        if (cache.containsKey(path)) {
            return (ConfigurationSection) cache.get(path);
        } else {
            cache.put(path, config.getConfigurationSection(path));
            return getSectionOrNull(path);
        }
    }

    @Override
    @NotNull
    public Config getSubsection(@NotNull final String path) {
        return new ConfigSection(this.getSection(path));
    }

    @Override
    @Nullable
    public Config getSubsectionOrNull(@NotNull final String path) {
        ConfigurationSection section = this.getSectionOrNull(path);
        if (section == null) {
            return null;
        }
        return new ConfigSection(section);
    }

    @Override
    public int getInt(@NotNull final String path) {
        if (cache.containsKey(path)) {
            return (int) cache.get(path);
        } else {
            cache.put(path, config.getInt(path, 0));
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
            cache.put(path, config.getInt(path, def));
            return getInt(path);
        }
    }

    @Override
    @NotNull
    public List<Integer> getInts(@NotNull final String path) {
        if (cache.containsKey(path)) {
            return (List<Integer>) cache.get(path);
        } else {
            cache.put(path, has(path) ? new ArrayList<>(config.getIntegerList(path)) : new ArrayList<>());
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
            cache.put(path, config.getBoolean(path));
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
            cache.put(path, has(path) ? new ArrayList<>(config.getBooleanList(path)) : new ArrayList<>());
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
            cache.put(path, StringUtils.translate(Objects.requireNonNull(config.getString(path, ""))));
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
            return (List<String>) cache.get(path);
        } else {
            cache.put(path, has(path) ? new ArrayList<>(config.getStringList(path)) : new ArrayList<>());
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
            cache.put(path, config.getDouble(path));
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
            cache.put(path, has(path) ? new ArrayList<>(config.getDoubleList(path)) : new ArrayList<>());
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
