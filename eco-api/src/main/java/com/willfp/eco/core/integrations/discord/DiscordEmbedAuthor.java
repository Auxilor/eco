package com.willfp.eco.core.integrations.discord;

import org.jetbrains.annotations.Nullable;

/**
 * Represents the author block of a Discord embed (name, optional url and icon).
 */
public final class DiscordEmbedAuthor {
    /** The author name shown in the embed. */
    public final String name;

    /** Optional URL to link the author name to. */
    @Nullable
    public final String url;

    /** Optional icon URL for the author. */
    @Nullable
    public final String icon_url;

    /**
     * Create a new author block.
     *
     * @param name     The display name of the author.
     * @param url      Optional URL for the author name.
     * @param icon_url Optional icon URL.
     */
    public DiscordEmbedAuthor(final String name, @Nullable final String url, @Nullable final String icon_url) {
        this.name = name;
        this.url = url;
        this.icon_url = icon_url;
    }
}

