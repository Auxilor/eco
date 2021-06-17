package com.willfp.eco.internal.config.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.willfp.eco.core.config.Config;
import com.willfp.eco.core.config.JSONConfig;
import com.willfp.eco.util.StringUtils;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"unchecked", "unused"})
public abstract class JSONConfigWrapper implements JSONConfig, Cloneable {
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
     * All cached values.
     */
    @Getter
    private final Map<String, Object> cache = new HashMap<>();

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
        cache.clear();
    }

    @Override
    public String toPlaintext() {
        return this.getHandle().toJson(this.getValues());
    }

    @Override
    public boolean has(@NotNull final String path) {
        return getOfKnownType(path, Object.class) != null;
    }

    @Nullable
    protected <T> T getOfKnownType(@NotNull final String path,
                                   @NotNull final Class<T> clazz) {
        return getOfKnownType(path, clazz, true);
    }

    @Nullable
    protected <T> T getOfKnownType(@NotNull final String path,
                                   @NotNull final Class<T> clazz,
                                   final boolean isBase) {
        String closestPath = path;

        if (cache.containsKey(path) && isBase) {
            return (T) cache.get(path);
        }

        if (path.contains(".")) {
            String[] split = path.split("\\.");
            closestPath = split[0];
        }

        if (values.get(closestPath) instanceof Map && !path.equals(closestPath)) {
            JSONConfigSection section = new JSONConfigSection((Map<String, Object>) values.get(closestPath));
            return section.getOfKnownType(path.substring(closestPath.length() + 1), clazz, false);
        } else {
            if (values.containsKey(closestPath)) {
                return (T) values.get(closestPath);
            } else {
                return null;
            }
        }
    }

    @NotNull
    @Override
    public List<String> getKeys(final boolean deep) {
        if (deep) {
            return new ArrayList<>(getDeepKeys(new HashSet<>(), ""));
        } else {
            return new ArrayList<>(values.keySet());
        }
    }

    protected Set<String> getDeepKeys(@NotNull final Set<String> list,
                                      @NotNull final String root) {
        for (String key : values.keySet()) {
            list.add(root + key);

            if (values.get(key) instanceof Map) {
                JSONConfigSection section = new JSONConfigSection((Map<String, Object>) values.get(key));
                list.addAll(section.getDeepKeys(list, root + key + "."));
            }
        }

        return list;
    }

    @Override
    @Nullable
    public Object get(@NotNull final String path) {
        return getOfKnownType(path, Object.class);
    }

    @Override
    public void set(@NotNull final String path,
                    @Nullable final Object object) {
        setRecursively(path, object, true);
        clearCache();
    }

    protected void setRecursively(@NotNull final String path,
                                  @Nullable final Object object,
                                  final boolean isBase) {
        String closestPath = path;

        if (path.contains(".")) {
            String[] split = path.split("\\.");
            closestPath = split[0];
        }

        if (values.get(closestPath) instanceof Map && !path.equals(closestPath)) {
            JSONConfigSection section = new JSONConfigSection((Map<String, Object>) values.get(closestPath));
            section.setRecursively(path.substring(closestPath.length() + 1), object, false);
            values.put(closestPath, section.getValues());
        } else {
            Object obj = object;

            if (object instanceof JSONConfig) {
                obj = ((JSONConfigWrapper) object).getValues();
            }

            values.put(path, obj);
        }
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
        if (values.containsKey(path)) {
            Map<String, Object> subsection = (Map<String, Object>) values.get(path);
            return new JSONConfigSection(subsection);
        } else {
            return null;
        }
    }

    @Override
    @NotNull
    public List<JSONConfig> getSubsections(@NotNull final String path) {
        List<JSONConfig> subsections = getSubsectionsOrNull(path);
        Validate.notNull(subsections);
        return subsections;
    }

    @Override
    @Nullable
    public List<JSONConfig> getSubsectionsOrNull(@NotNull final String path) {
        List<Map<String, Object>> maps = (List<Map<String, Object>>) getOfKnownType(path, Object.class);

        if (maps == null) {
            return null;
        }

        List<JSONConfig> configs = new ArrayList<>();

        for (Map<String, Object> map : maps) {
            configs.add(new JSONConfigSection(map));
        }

        return configs;
    }

    @Override
    public int getInt(@NotNull final String path) {
        // ew
        return Objects.requireNonNullElse(getOfKnownType(path, Double.class), 0D).intValue();
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
        return Objects.requireNonNullElse(getOfKnownType(path, Integer.class), def);
    }

    @Override
    @NotNull
    public List<Integer> getInts(@NotNull final String path) {
        return (List<Integer>) Objects.requireNonNullElse(getOfKnownType(path, Object.class), new ArrayList<>());
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
        return Objects.requireNonNullElse(getOfKnownType(path, Boolean.class), false);
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
        return (List<Boolean>) Objects.requireNonNullElse(getOfKnownType(path, Object.class), new ArrayList<>());
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
        return StringUtils.translate(Objects.requireNonNullElse(getOfKnownType(path, String.class), ""));
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
        return StringUtils.translateList((List<String>) Objects.requireNonNullElse(getOfKnownType(path, Object.class), new ArrayList<>()));
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
        return Objects.requireNonNullElse(getOfKnownType(path, Double.class), 0D);
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
        return (List<Double>) Objects.requireNonNullElse(getOfKnownType(path, Object.class), new ArrayList<>());
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

    @Override
    public JSONConfigWrapper clone() {
        return new JSONConfigSection(new HashMap<>(this.getValues()));
    }
}
