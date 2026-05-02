package com.willfp.eco.core.integrations.discord;

/**
 * Simple wrapper for media (image/thumbnail) used in Discord embeds.
 */
public final class DiscordEmbedMedia {
    /** URL of the media resource. */
    public final String url;

    /**
     * Create a media object.
     *
     * @param url The URL of the media.
     */
    public DiscordEmbedMedia(final String url) {
        this.url = url;
    }
}

