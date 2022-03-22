package com.willfp.eco.core.config.interfaces;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * JSON config.
 *
 * @deprecated JSON and yml have full parity, use configs without a prefix instead,
 * eg {@link com.willfp.eco.core.config.TransientConfig}, {@link com.willfp.eco.core.config.BaseConfig}.
 * These configs will be removed eventually.
 */
@Deprecated(since = "6.17.0", forRemoval = true)
@ApiStatus.ScheduledForRemoval(inVersion = "6.30.0")
public interface JSONConfig extends Config {
    /**
     * Get a list of subsections from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or a blank {@link java.util.ArrayList} if not found.
     */
    @NotNull
    default List<JSONConfig> getSubsections(@NotNull String path) {
        return Objects.requireNonNullElse(getSubsectionsOrNull(path), new ArrayList<>());
    }

    /**
     * Get a list of subsections from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    List<JSONConfig> getSubsectionsOrNull(@NotNull String path);


    /**
     * Get subsection from config.
     *
     * @param path The key to check.
     * @return The subsection. Throws NPE if not found.
     */
    @Override
    @NotNull
    JSONConfig getSubsection(@NotNull String path);

    /**
     * Get subsection from config.
     *
     * @param path The key to check.
     * @return The subsection, or null if not found.
     */
    @Override
    @Nullable
    JSONConfig getSubsectionOrNull(@NotNull String path);

    @Override
    JSONConfig clone();
}
