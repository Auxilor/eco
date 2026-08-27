package com.willfp.eco.core.datapack;

import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * The outcome of a datapack install or removal.
 * <p>
 * Nothing in the datapack layer throws on bad content: callers are handed an
 * {@link InstallResult} instead. Exceptions are reserved for programmer error.
 *
 * @param status   The status.
 * @param messages Human-readable detail, e.g. validation errors.
 */
public record InstallResult(@NotNull Status status,
                            @NotNull List<String> messages) {
    /**
     * Create an install result.
     *
     * @param status   The status.
     * @param messages Human-readable detail.
     */
    public InstallResult {
        messages = List.copyOf(messages);
    }

    /**
     * Create an install result with no messages.
     *
     * @param status The status.
     */
    public InstallResult(@NotNull final Status status) {
        this(status, List.of());
    }

    /**
     * If nothing failed.
     *
     * @return If the operation did not fail.
     */
    public boolean succeeded() {
        return this.status != Status.FAILED;
    }

    /**
     * If the pack on disk was modified.
     *
     * @return If the pack changed.
     */
    public boolean changed() {
        return this.status == Status.READY || this.status == Status.RESTART_REQUIRED;
    }

    /**
     * If a server restart is needed for the content to take effect.
     *
     * @return If a restart is required.
     */
    public boolean restartRequired() {
        return this.status == Status.RESTART_REQUIRED;
    }

    /**
     * The status of an install.
     */
    public enum Status {
        /**
         * Nothing was written. The live pack is untouched.
         */
        FAILED,

        /**
         * The content was byte-identical to what was already on disk.
         */
        UNCHANGED,

        /**
         * The pack was written and the content is live (or will be on the next
         * datapack reload).
         */
        READY,

        /**
         * The pack was written, but the content is bootstrap-only and will not
         * take effect until the server restarts.
         */
        RESTART_REQUIRED
    }
}
