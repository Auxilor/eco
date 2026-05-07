package com.willfp.eco.core.integrations.discord;

import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * Data class representing the payload that can be sent to Discord webhooks.
 * Contains simple message fields and optional embeds.
 */
public final class DiscordWebhookMessage {
    /** Message content (plain text). Max length enforced by Discord. */
    @Nullable
    public final String content;

    /** Optional username to override the webhook default. */
    @Nullable
    public final String username;

    /** Optional avatar URL to override the webhook default. */
    @Nullable
    public final String avatar_url;

    /** Whether the message should be read as TTS (text-to-speech). */
    @Nullable
    public final Boolean tts;

    /** Optional list of embeds attached to the message. */
    @Nullable
    public final List<DiscordEmbed> embeds;

    /**
     * Create a webhook message payload.
     *
     * @param content    The message content.
     * @param username   Optional username override.
     * @param avatar_url Optional avatar URL override.
     * @param tts        Optional TTS flag.
     * @param embeds     Optional embeds list.
     */
    public DiscordWebhookMessage(@Nullable final String content,
                                 @Nullable final String username,
                                 @Nullable final String avatar_url,
                                 @Nullable final Boolean tts,
                                 @Nullable final List<DiscordEmbed> embeds) {
        this.content = content;
        this.username = username;
        this.avatar_url = avatar_url;
        this.tts = tts;
        this.embeds = embeds;
    }
}


