package com.willfp.eco.core.config.wrapper;

import com.willfp.eco.core.config.interfaces.Config;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
    @Getter
    private final T handle;

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
    public @NotNull String getString(@NotNull final String path) {
        return handle.getString(path);
    }

    @Override
    public @Nullable String getStringOrNull(@NotNull final String path) {
        return handle.getStringOrNull(path);
    }

    @Override
    public @NotNull List<String> getStrings(@NotNull final String path) {
        return handle.getStrings(path);
    }

    @Override
    public @NotNull List<String> getStrings(@NotNull final String path,
                                            final boolean format) {
        return handle.getStrings(path, format);
    }

    @Override
    public @Nullable List<String> getStringsOrNull(@NotNull final String path) {
        return handle.getStringsOrNull(path);
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
    public Config clone() {
        return handle.clone();
    }
}
