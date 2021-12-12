package com.willfp.eco.core.config.interfaces;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * JSON config.
 *
 * @deprecated JSON and yml have full parity, use configs without a prefix instead,
 * eg {@link com.willfp.eco.core.config.TransientConfig}, {@link com.willfp.eco.core.config.BaseConfig}.
 * These configs will be removed eventually.
 */
@Deprecated(forRemoval = true)
@SuppressWarnings("DeprecatedIsStillUsed")
public interface JSONConfig extends Config {
    /**
     * Get a list of subsections from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or a blank {@link java.util.ArrayList} if not found.
     */
    @NotNull
    List<JSONConfig> getSubsections(@NotNull String path);

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
