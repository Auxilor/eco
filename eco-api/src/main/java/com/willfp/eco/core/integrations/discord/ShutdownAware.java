package com.willfp.eco.core.integrations.discord;

/**
 * Implemented by {@link DiscordIntegration} instances that manage background worker threads
 * and need to be shut down cleanly when the platform plugin is disabled.
 */
public interface ShutdownAware {
    /**
     * Signal the implementation to stop accepting new requests and drain any remaining
     * queued work before terminating its worker thread.
     */
    void shutdown();
}

