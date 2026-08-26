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
     * Refused if any entry has been committed, i.e. has been part of an
     * enabled pack on a world that has been loaded. Removing bootstrap-only
     * content that a world has already generated with corrupts that world.
     *
     * @return The outcome.
     */
    @NotNull
    InstallResult remove();

    /**
     * Remove the whole pack, ignoring committed entries.
     * <p>
     * <strong>This can corrupt worlds.</strong> Chunks that reference a biome
     * or dimension ID which no longer resolves will fail to load. Only call
     * this in response to an explicit, informed admin action.
     *
     * @return The outcome.
     */
    @NotNull
    InstallResult forceRemove();

    /**
     * If this plugin has written bootstrap-only content that is not yet live.
     *
     * @return If a restart is pending for this plugin.
     */
    boolean restartPending();
}
