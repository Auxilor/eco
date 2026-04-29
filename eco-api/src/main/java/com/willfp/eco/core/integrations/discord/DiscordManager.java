package com.willfp.eco.core.integrations.discord;

import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

/**
 * Simple manager for Discord webhook implementation. Unlike other integrations, Discord webhooks
 * are always available and therefore do not use the IntegrationRegistry. The actual implementation
 * must be registered by the platform module (core-plugin).
 */
public final class DiscordManager {
    private static volatile DiscordIntegration implementation = null;

    private DiscordManager() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Register the implementation to use. Called by the platform module.
     *
     * @param integration The implementation of {@link DiscordIntegration} to register.
     */
    public static void register(@NotNull final DiscordIntegration integration) {
        implementation = integration;
    }

    /**
     * Execute a Discord webhook using a JSON payload.
     * <p>
     * If no implementation has been registered via {@link #register(DiscordIntegration)}, this
     * method returns a completed future with a null value.
     *
     * @param webhookUrl Full webhook URL (including id and token).
     * @param message    The message payload to send.
     * @param wait       If true, request Discord to return the created message JSON.
     * @return A CompletableFuture containing the response body when available, or null on failure.
     */
    public static CompletableFuture<String> executeWebhookJson(@NotNull final String webhookUrl,
                                                               @NotNull final DiscordWebhookMessage message,
                                                               final boolean wait) {
        if (implementation == null) return CompletableFuture.completedFuture(null);
        return implementation.executeWebhookJson(webhookUrl, message, wait);
    }

    /**
     * Execute a Discord webhook with a single file attachment (multipart/form-data).
     *
     * @param webhookUrl Full webhook URL (including id and token).
     * @param message    The message payload (sent as payload_json part).
     * @param fileName   Filename to send for the attachment.
     * @param fileBytes  Attachment contents as a byte array.
     * @param mimeType   Attachment mime type (e.g. "image/png").
     * @param wait       If true, request Discord to return the created message JSON.
     * @return A CompletableFuture containing the response body when available, or null on failure.
     */
    public static CompletableFuture<String> executeWebhookWithFile(@NotNull final String webhookUrl,
                                                                    @NotNull final DiscordWebhookMessage message,
                                                                    @NotNull final String fileName,
                                                                    final byte[] fileBytes,
                                                                    @NotNull final String mimeType,
                                                                    final boolean wait) {
        if (implementation == null) return CompletableFuture.completedFuture(null);
        return implementation.executeWebhookWithFile(webhookUrl, message, fileName, fileBytes, mimeType, wait);
    }

    // Convenience wrappers
    /**
     * Send a plain content message to the webhook asynchronously (no wait).
     *
     * @param webhookUrl Full webhook URL.
     * @param content    The message content to send.
     * @return A CompletableFuture that completes when the request has been dispatched. The
     * contained value is the response body when available, or null.
     */
    public static CompletableFuture<String> sendContent(@NotNull final String webhookUrl,
                                                        @NotNull final String content) {
        return executeWebhookJson(webhookUrl, new DiscordWebhookMessage(content, null, null, null, null), false);
    }

    /**
     * Send a plain content message to the webhook and wait for Discord to return the created
     * message JSON.
     *
     * @param webhookUrl Full webhook URL.
     * @param content    The message content to send.
     * @return A CompletableFuture containing the created message JSON, or null on failure.
     */
    public static CompletableFuture<String> sendContentWithWait(@NotNull final String webhookUrl,
                                                                @NotNull final String content) {
        return executeWebhookJson(webhookUrl, new DiscordWebhookMessage(content, null, null, null, null), true);
    }

    /**
     * Shut down the registered implementation's worker thread, draining any remaining queued
     * requests first. Should be called when the platform plugin is disabled.
     */
    public static void shutdown() {
        if (implementation instanceof ShutdownAware) {
            ((ShutdownAware) implementation).shutdown();
        }
    }
}


