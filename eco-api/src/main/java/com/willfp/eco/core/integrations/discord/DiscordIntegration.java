package com.willfp.eco.core.integrations.discord;

import com.willfp.eco.core.integrations.Integration;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Integration point for Discord webhooks.
 */
public interface DiscordIntegration extends Integration {
    /**
     * Execute a webhook with a JSON payload.
     *
     * @param webhookUrl The full webhook URL (id/token).
     * @param message    The message payload.
     * @param wait       If true, ask Discord to return the created message JSON.
     * @return A CompletableFuture containing the response body when available, or null on failure.
     */
    @NotNull
    CompletableFuture<@Nullable String> executeWebhookJson(@NotNull String webhookUrl,
                                                            @NotNull DiscordWebhookMessage message,
                                                            boolean wait);

    /**
     * Execute a webhook with a single file attachment.
     *
     * @param webhookUrl The full webhook URL.
     * @param message    The message payload (payload_json).
     * @param fileName   The filename to send.
     * @param fileBytes  The file contents as bytes.
     * @param mimeType   The file mime type (e.g. image/png).
     * @param wait       If true, ask Discord to return the created message JSON.
     * @return A CompletableFuture containing the response body when available, or null on failure.
     */
    @NotNull
    CompletableFuture<@Nullable String> executeWebhookWithFile(@NotNull String webhookUrl,
                                                                @NotNull DiscordWebhookMessage message,
                                                                @NotNull String fileName,
                                                                byte[] fileBytes,
                                                                @NotNull String mimeType,
                                                                boolean wait);
}


