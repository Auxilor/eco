package com.willfp.eco.core.datapack;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * Accumulates the entries that make up a datapack, before it is published.
 * <p>
 * Content is opaque: the datapack layer never parses, models or understands
 * datapack schemas beyond checking that they can be decoded by the server. It
 * is the plugin's job to emit content that is correct for the running version.
 */
public interface DatapackDraft {
    /**
     * Add a text entry, e.g. JSON or mcfunction source.
     * <p>
     * JSON entries are canonicalised before writing, so whitespace and key
     * order do not affect whether the pack is considered changed.
     *
     * @param registry The directory under {@code data/<namespace>/}, for
     *                 example {@code worldgen/biome} or {@code recipe}.
     * @param id       The entry ID.
     * @param content  The file content.
     * @return This, for chaining.
     */
    @NotNull
    DatapackDraft put(@NotNull String registry,
                      @NotNull NamespacedKey id,
                      @NotNull String content);

    /**
     * Add a binary entry, e.g. an {@code .nbt} structure.
     * <p>
     * Binary entries are written as given and compared as given: no
     * canonicalisation is possible.
     *
     * @param registry The directory under {@code data/<namespace>/}.
     * @param id       The entry ID.
     * @param content  The file content.
     * @return This, for chaining.
     */
    @NotNull
    DatapackDraft put(@NotNull String registry,
                      @NotNull NamespacedKey id,
                      byte @NotNull [] content);
}
