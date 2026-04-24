package com.willfp.eco.core.integrations.hologram;

import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper class for plugin-specific holograms.
 */
public interface Hologram {
    /**
     * Remove the hologram.
     */
    void remove();

    /**
     * Set the hologram contents.
     *
     * @param contents The contents.
     */
    void setContents(@NotNull List<String> contents);
}
