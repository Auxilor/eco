package com.willfp.eco.internal.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.Config;
import com.willfp.eco.util.StringUtils;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"unchecked", "unused"})
public abstract class JSONConfigWrapper implements Config {
    /**
     * The linked {@link ConfigurationSection} where values are physically stored.
     */
    @Getter
    private final Gson handle = new GsonBuilder().setPrettyPrinting().create();

    /**
     * All stored values.
     */
    @Getter
    private final Map<String, Object> values = new HashMap<>();

    /**
     * Abstract config.
     */
    protected JSONConfigWrapper() {

    }

    public void init(@NotNull final Map<String, Object> values) {
        this.values.clear();
        this.values.putAll(values);
    }

    @Override
    public final void clearCache() {
        // Do nothing.
    }

    @Override
    public boolean has(@NotNull final String path) {
        return values.containsKey(path);
    }

    @NotNull
    @Override
    public List<String> getKeys(final boolean deep) {
        return new ArrayList<>(values.keySet());
    }

    @Override
    @Nullable
    public Object get(@NotNull final String path) {
        return values.get(path);
    }

    @Override
    public void set(@NotNull final String path,
                    @Nullable final Object object) {
        values.put(path, object);
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

    }

    @Override
    public int getInt(@NotNull final String path) {
        if (values.containsKey(path)) {
            return (int) values.get(path);
        } else {
            values.put(path, 0);
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
        if (values.containsKey(path)) {
            return (int) values.get(path);
        } else {
            values.put(path, def);
            return getInt(path);
        }
    }

    @Override
    @NotNull
    public List<Integer> getInts(@NotNull final String path) {
        if (values.containsKey(path)) {
            return (List<Integer>) values.get(path);
        } else {
            values.put(path, 0);
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
        if (values.containsKey(path)) {
            return (Boolean) values.get(path);
        } else {
            values.put(path, 0);
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
        if (values.containsKey(path)) {
            return (List<Boolean>) values.get(path);
        } else {
            values.put(path, 0);
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
        if (values.containsKey(path)) {
            return (String) values.get(path);
        } else {
            values.put(path, 0);
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
        if (values.containsKey(path)) {
            return (List<String>) values.get(path);
        } else {
            values.put(path, 0);
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
        if (values.containsKey(path)) {
            return (double) values.get(path);
        } else {
            values.put(path, 0);
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
        if (values.containsKey(path)) {
            return (List<Double>) values.get(path);
        } else {
            values.put(path, 0);
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
