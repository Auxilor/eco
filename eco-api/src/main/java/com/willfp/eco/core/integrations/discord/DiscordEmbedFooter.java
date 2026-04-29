package com.willfp.eco.core.integrations.discord;

import org.jetbrains.annotations.Nullable;

/**
 * Footer block for a Discord embed containing text and an optional icon URL.
 */
public final class DiscordEmbedFooter {
    /** Footer text shown in the embed. */
    public final String text;

    /** Optional icon URL displayed next to the footer text. */
    @Nullable
    public final String icon_url;

    /**
     * Create a footer block.
     *
     * @param text     Footer text.
     * @param icon_url Optional icon URL.
     */
    public DiscordEmbedFooter(final String text, @Nullable final String icon_url) {
        this.text = text;
        this.icon_url = icon_url;
    }
}

