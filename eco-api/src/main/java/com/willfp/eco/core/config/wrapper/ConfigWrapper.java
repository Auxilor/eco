package com.willfp.eco.core.config.wrapper;

import com.willfp.eco.core.config.ConfigType;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.placeholder.InjectablePlaceholder;
import com.willfp.eco.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Configs from eco have an internal implementation,
 * which is the handle.
 * <p>
 * This class handles them.
 *
 * @param <T> The type of the handle.
 */
@SuppressWarnings({"MethodDoesntCallSuperMethod", "removal"})
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
    @Deprecated(since = "6.31.1", forRemoval = true)
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
    public @NotNull List<String> recurseKeys(@NotNull final Set<String> found,
                                             @NotNull final String root) {
        return handle.recurseKeys(found, root);
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
    public @Nullable Config getSubsectionOrNull(@NotNull final String path) {
        return handle.getSubsectionOrNull(path);
    }

    @Override
    public @Nullable Integer getIntOrNull(@NotNull final String path) {
        return handle.getIntOrNull(path);
    }

    @Override
    public @Nullable List<Integer> getIntsOrNull(@NotNull final String path) {
        return handle.getIntsOrNull(path);
    }

    @Override
    public @Nullable Boolean getBoolOrNull(@NotNull final String path) {
        return handle.getBoolOrNull(path);
    }

    @Override
    public @Nullable List<Boolean> getBoolsOrNull(@NotNull final String path) {
        return handle.getBoolsOrNull(path);
    }

    @Override
    public @Nullable String getStringOrNull(@NotNull final String path,
                                            final boolean format,
                                            @NotNull final StringUtils.FormatOption option) {
        return handle.getStringOrNull(path, format, option);
    }

    @Override
    public @Nullable List<String> getStringsOrNull(@NotNull final String path,
                                                   final boolean format,
                                                   @NotNull final StringUtils.FormatOption option) {
        return handle.getStringsOrNull(path, format, option);
    }

    @Override
    public @Nullable Double getDoubleOrNull(@NotNull final String path) {
        return handle.getDoubleOrNull(path);
    }

    @Override
    public @Nullable List<Double> getDoublesOrNull(@NotNull final String path) {
        return handle.getDoublesOrNull(path);
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
    public @NotNull ConfigType getType() {
        return handle.getType();
    }

    @Override
    public void addInjectablePlaceholder(@NotNull final Iterable<InjectablePlaceholder> placeholders) {
        handle.addInjectablePlaceholder(placeholders);
    }

    @Override
    public @NotNull List<InjectablePlaceholder> getPlaceholderInjections() {
        return handle.getPlaceholderInjections();
    }

    @Override
    public void clearInjectedPlaceholders() {
        handle.clearInjectedPlaceholders();
    }

    @Override
    public Map<String, Object> toMap() {
        return this.handle.toMap();
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
