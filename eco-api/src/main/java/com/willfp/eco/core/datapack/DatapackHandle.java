package com.willfp.eco.core.datapack;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * A single plugin's datapack.
 * <p>
 * Each plugin owns exactly one pack, written to
 * {@code <world container>/<level-name>/datapacks/eco_<plugin id>/}. One pack
 * per plugin means no file-level conflicts between plugins, and one plugin's
 * bad pack cannot corrupt another's.
 */
public interface DatapackHandle {
    /**
     * Build and publish the pack.
     * <p>
     * The whole pack is rebuilt from the draft: entries not added by the
     * builder are removed. The write is atomic. If any entry fails validation,
     * nothing is written and the live pack is left untouched.
     * <p>
     * This never throws on bad content.
     *
     * @param builder Populates the draft.
     * @return The outcome.
     */
    @NotNull
    InstallResult apply(@NotNull Consumer<DatapackDraft> builder);

    /**
     * Remove the whole pack.
     * <p>
     * <strong>This can corrupt worlds.</strong> Removing bootstrap-only
     * content that a world has already generated with leaves chunks
     * referencing a biome or dimension ID that no longer resolves, and those
     * chunks will fail to load.
     * <p>
     * Committed entries, i.e. those that have been part of an enabled pack on
     * a loaded world, are logged before they go, but removal is never refused.
     * The same content can be dropped by a config edit and a rebuild, so
     * refusing here would only move the problem somewhere quieter.
     *
     * @return The outcome.
     */
    @NotNull
    InstallResult remove();

    /**
     * If this plugin has written bootstrap-only content that is not yet live.
     *
     * @return If a restart is pending for this plugin.
     */
    boolean restartPending();
}
