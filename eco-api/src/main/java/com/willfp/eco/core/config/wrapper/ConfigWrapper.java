package com.willfp.eco.core.config.wrapper;

import com.willfp.eco.core.config.ConfigType;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.util.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Configs from eco have an internal implementation,
 * which is the handle.
 * <p>
 * This class handles them.
 *
 * @param <T> The type of the handle.
 */
public abstract class ConfigWrapper<T extends Config> implements Config {
    /**
     * Configs from eco have an internal implementation,
     * which is the handle.
     * <p>
     * The handle should only ever be used if you want to
     * do something <i>interesting</i> config-wise with some
     * internals.
     * <p>
     * In general use, though, the handle isn't necessary.
     */
    private final T handle;

    /**
     * Create a config wrapper.
     *
     * @param handle The config that is being wrapped.
     */
    protected ConfigWrapper(@NotNull final T handle) {
        this.handle = handle;
    }

    @Override
    public void clearCache() {
        handle.clearCache();
    }

    @Override
    public String toPlaintext() {
        return handle.toPlaintext();
    }

    @Override
    public boolean has(@NotNull final String path) {
        return handle.has(path);
    }

    @Override
    public @NotNull List<String> getKeys(final boolean deep) {
        return handle.getKeys(deep);
    }

    @Override
    public @Nullable Object get(@NotNull final String path) {
        return handle.get(path);
    }

    @Override
    public void set(@NotNull final String path,
                    @Nullable final Object object) {
        handle.set(path, object);
    }

    @Override
    public @NotNull Config getSubsection(@NotNull final String path) {
        return handle.getSubsection(path);
    }

    @Override
    public @Nullable Config getSubsectionOrNull(@NotNull final String path) {
        return handle.getSubsectionOrNull(path);
    }

    @Override
    public int getInt(@NotNull final String path) {
        return handle.getInt(path);
    }

    @Override
    public @Nullable Integer getIntOrNull(@NotNull final String path) {
        return handle.getIntOrNull(path);
    }

    @Override
    public int getInt(@NotNull final String path,
                      final int def) {
        return handle.getInt(path, def);
    }

    @Override
    public @NotNull List<Integer> getInts(@NotNull final String path) {
        return handle.getInts(path);
    }

    @Override
    public @Nullable List<Integer> getIntsOrNull(@NotNull final String path) {
        return handle.getIntsOrNull(path);
    }

    @Override
    public boolean getBool(@NotNull final String path) {
        return handle.getBool(path);
    }

    @Override
    public @Nullable Boolean getBoolOrNull(@NotNull final String path) {
        return handle.getBoolOrNull(path);
    }

    @Override
    public @NotNull List<Boolean> getBools(@NotNull final String path) {
        return handle.getBools(path);
    }

    @Override
    public @Nullable List<Boolean> getBoolsOrNull(@NotNull final String path) {
        return handle.getBoolsOrNull(path);
    }


    @Override
    public @NotNull String getString(@NotNull final String path,
                                     final boolean format,
                                     @NotNull final StringUtils.FormatOption option) {
        return handle.getString(path, format, option);
    }

    @Override
    public @Nullable String getStringOrNull(@NotNull final String path,
                                            final boolean format,
                                            @NotNull final StringUtils.FormatOption option) {
        return handle.getStringOrNull(path, format, option);
    }

    @Override
    public @NotNull List<String> getStrings(@NotNull final String path,
                                            final boolean format,
                                            @NotNull final StringUtils.FormatOption option) {
        return handle.getStrings(path, format, option);
    }

    @Override
    public @Nullable List<String> getStringsOrNull(@NotNull final String path,
                                                   final boolean format,
                                                   @NotNull final StringUtils.FormatOption option) {
        return handle.getStringsOrNull(path, format, option);
    }

    @Override
    public double getDouble(@NotNull final String path) {
        return handle.getDouble(path);
    }

    @Override
    public @Nullable Double getDoubleOrNull(@NotNull final String path) {
        return handle.getDoubleOrNull(path);
    }

    @Override
    public @NotNull List<Double> getDoubles(@NotNull final String path) {
        return handle.getDoubles(path);
    }

    @Override
    public @Nullable List<Double> getDoublesOrNull(@NotNull final String path) {
        return handle.getDoublesOrNull(path);
    }

    @Override
    public @NotNull List<? extends Config> getSubsections(@NotNull final String path) {
        return handle.getSubsections(path);
    }

    @Override
    public @Nullable List<? extends Config> getSubsectionsOrNull(@NotNull final String path) {
        return handle.getSubsectionsOrNull(path);
    }

    @Override
    public Config clone() {
        return handle.clone();
    }

    @Override
    public @Nullable YamlConfiguration getBukkitHandle() {
        return handle.getBukkitHandle();
    }

    @Override
    public @NotNull ConfigType getType() {
        return handle.getType();
    }

    /**
     * Get the handle.
     *
     * @return The handle.
     */
    public T getHandle() {
        return this.handle;
    }
}
